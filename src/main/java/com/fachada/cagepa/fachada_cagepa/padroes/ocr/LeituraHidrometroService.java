package com.fachada.cagepa.fachada_cagepa.padroes.ocr;

import com.fachada.cagepa.fachada_cagepa.infra.entities.LeituraDoHidrometro;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.HidrometroRepository;
import com.fachada.cagepa.fachada_cagepa.infra.repositories.LeituraDoHidrometroRepository;
import com.fachada.cagepa.fachada_cagepa.padroes.adapter.OCRAdapter;
import com.fachada.cagepa.fachada_cagepa.padroes.fachada.proxy.AdminProxyFachada;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeituraHidrometroService {

    private final LeituraDoHidrometroRepository repository;
    private final OCRAdapter ocrAdapter;
    private final AdminProxyFachada adminProxyFachada;
    private final HidrometroRepository hidrometroRepository;

    public LeituraHidrometroService(LeituraDoHidrometroRepository repository, OCRAdapter ocrAdapter,
                                    AdminProxyFachada adminProxyFachada, HidrometroRepository hidrometroRepository) {
        this.repository = repository;
        this.ocrAdapter = ocrAdapter;
        this.adminProxyFachada = adminProxyFachada;
        this.hidrometroRepository = hidrometroRepository;
    }

    @Transactional
    public void processarImagemHidrometro(File imageFile) {
        try {
            System.out.println("==> Iniciando processamento: " + imageFile.getName());

            // Validar se admin está autenticado
            if (!adminProxyFachada.estaAutenticado()) {
                System.err.println("Aviso: Admin nao autenticado. Leitura nao sera registrada: " + imageFile.getName());
                return;
            }

            if (!imageFile.exists() || !imageFile.isFile()) {
                System.err.println("Aviso: Arquivo nao encontrado ou invalido: " + imageFile.getAbsolutePath());
                return;
            }

            String filename = imageFile.getName();
            String shaHidrometro = ocrAdapter.extractMeterId(filename);
            System.out.println("==> SHA extraido: " + shaHidrometro + " (de: " + filename + ")");

            // Validar se o SHA está cadastrado no banco
            if (!hidrometroRepository.existsBySha(shaHidrometro)) {
                System.err.println("Aviso: SHA do hidrometro nao encontrado no banco de dados: " + shaHidrometro);
                return;
            }

            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                System.err.println("Aviso: Nao foi possivel ler a imagem: " + filename);
                return;
            }

            Long valorLeitura = ocrAdapter.tryExtractMeterValue(image);
            System.out.println("==> Valor extraido: " + valorLeitura);

            if (valorLeitura == null) {
                System.err.println("Aviso: Nao foi possivel extrair o valor do hidrometro: " + filename);
                return;
            }

            if (!isValueGreaterThanLastReading(shaHidrometro, valorLeitura)) {
                return;
            }

            String tipoHidrometro = ocrAdapter.determineMeterType(image);
            System.out.println("==> Tipo: " + tipoHidrometro);

            LeituraDoHidrometro leitura = LeituraDoHidrometro.builder()
                    .shaHidrometro(shaHidrometro)
                    .valorLeitura(valorLeitura)
                    .timestamp(LocalDateTime.now())
                    .tipoHidrometro(tipoHidrometro)
                    .build();

            repository.save(leitura);
            System.out.println("==> Leitura registrada com sucesso: SHA=" + shaHidrometro + ", Valor=" + valorLeitura + ", Tipo=" + tipoHidrometro);

        } catch (Exception e) {
            System.err.println("Aviso: Erro ao processar imagem " + imageFile.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValueGreaterThanLastReading(String shaHidrometro, Long novoValor) {
        List<LeituraDoHidrometro> leituras = repository.findByShaHidrometro(shaHidrometro);
        
        if (leituras.isEmpty()) {
            return true;
        }

        Long ultimaLeitura = leituras.stream()
                .mapToLong(LeituraDoHidrometro::getValorLeitura)
                .max()
                .orElse(0L);

        return novoValor > ultimaLeitura;
    }
}
