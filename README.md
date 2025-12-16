# Padrões de Projeto GoF - Fachada CAGEPA

Este documento apresenta os padrões de projeto do Gang of Four (GoF) implementados no projeto Fachada CAGEPA, com suas localizações e descrições de uso.

---

## 📋 Índice

1. [Padrões Estruturais](#padrões-estruturais)
2. [Padrões Comportamentais](#padrões-comportamentais)
3. [Padrões Criacionais](#padrões-criacionais)

---

## Padrões Estruturais

### 🎭 **Facade (Fachada)**

**Descrição:** Fornece uma interface unificada e simplificada para um conjunto complexo de subsistemas, facilitando o uso de componentes complexos.

**Localização:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/fachada/PainelCagepaFacade.java`

**Responsabilidades:**
- Orquestra operações entre múltiplos proxies (AdminProxyFachada, ClienteProxyFachada, HidrometroProxyFachada)
- Gerencia o ciclo de vida do monitoramento de imagens
- Fornece uma API unificada para autenticação, gestão de clientes e hidrometros
- Integra o gerenciador de configurações

**Exemplo de Uso:**
```java
PainelCagepaFacade facade = applicationContext.getBean(PainelCagepaFacade.class);
facade.login("admin", "senha");  // Autentica e inicia monitoramento
facade.criarPessoaFisica("João", "12345678901", "joao@email.com", "11987654321");
facade.cadastrarHidrometroPessoaFisicaPorCPF("SHA123456...", 100L, "12345678901", 1L);
facade.logout();  // Desloga e para monitoramento
```

**Vantagens:**
- Decoupling entre cliente e subsistemas internos
- Simplificação de operações complexas
- Ponto centralizado para controle de fluxo

---

### 🔐 **Proxy**

**Descrição:** Fornece um substituto ou marcador de posição para controlar o acesso a outro objeto. Pode adicionar validações, autenticação ou controle de acesso antes de delegar operações.

**Localizações:**

#### 1. **AdminProxyFachada**
**Arquivo:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/proxy/AdminProxyFachada.java`

**Responsabilidades:**
- Controla acesso aos serviços de autenticação
- Valida credenciais e gera tokens JWT
- Gerencia o ciclo de vida da autenticação (login/logout)
- Integra o monitoramento ao processo de autenticação

**Exemplo:**
```java
AdminProxyFachada adminProxy = applicationContext.getBean(AdminProxyFachada.class);
adminProxy.login("admin", "password");      // Valida e autentica
adminProxy.criarAdministrador("user2", "pass2");  // Cria novo admin
adminProxy.estaAutenticado();                // Verifica estado
adminProxy.logout();                         // Limpa sessão
```

#### 2. **ClienteProxyFachada**
**Arquivo:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/proxy/ClienteProxyFachada.java`

**Responsabilidades:**
- Controla acesso aos serviços de clientes (Pessoa Física e Pessoa Jurídica)
- Valida dados antes de delegação aos serviços
- Fornece métodos para operações CRUD com clientes

**Exemplo:**
```java
ClienteProxyFachada clienteProxy = applicationContext.getBean(ClienteProxyFachada.class);
clienteProxy.criarPessoaFisica("João Silva", "12345678901", "joao@email.com", "11987654321");
clienteProxy.inativarPorCPF("12345678901");
PessoaFisica pessoa = clienteProxy.obterPorCPF("12345678901");
```

#### 3. **HidrometroProxyFachada**
**Arquivo:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/proxy/HidrometroProxyFachada.java`

**Responsabilidades:**
- Controla acesso aos serviços de hidrometros
- Valida SHA e dados técnicos
- Gerencia relacionamentos entre hidrometros e clientes/endereços

**Exemplo:**
```java
HidrometroProxyFachada hidrometroProxy = applicationContext.getBean(HidrometroProxyFachada.class);
hidrometroProxy.cadastrarHidrometroPessoaFisica("SHA123...", 100L, 1L, 1L);
Hidrometro hidrometro = hidrometroProxy.obterPorSHA("SHA123...");
List<Hidrometro> listagem = hidrometroProxy.listarPorPessoaFisica(1L);
```

**Vantagens:**
- Controle centralizado de acesso
- Validação antes de operações
- Facilita auditoria e logging

---

## Padrões Comportamentais

### 🎯 **Strategy (Estratégia)**

**Descrição:** Define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. Permite que o algoritmo varie independentemente dos clientes que o usam.

**Localização:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/strategy/`

**Interface:**
```
├── OcrStrategy.java              (Interface com 3 métodos)
├── ProprietarioStrategy.java     (Implementação para Pessoa Física)
└── ColaboradorStrategy.java      (Implementação para Pessoa Jurídica)
```

**Métodos da Interface:**
1. `extractMeterId(String filename)` - Extrai o SHA da imagem
2. `tryExtractMeterValue(BufferedImage image)` - Extrai o valor de consumo
3. `determineMeterType(BufferedImage image)` - Classifica o tipo de hidrometro

**Exemplo de Uso:**
```java
// Adapter escolhe a estratégia apropriada baseado no tipo de cliente
OcrStrategy strategy = obterEstrategiaParaCliente(clienteType);
String sha = strategy.extractMeterId(filename);
Integer valor = strategy.tryExtractMeterValue(image);
String tipo = strategy.determineMeterType(image);
```

**Implementações:**

#### **ProprietarioStrategy**
**Arquivo:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/strategy/ProprietarioStrategy.java`
- Estratégia para hidrometros de Pessoa Física
- Remove extensões de arquivo (.PNG, .JPEG, .JPG) do SHA
- Processa imagens específicas para leitura de valor

#### **ColaboradorStrategy**
**Arquivo:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/strategy/ColaboradorStrategy.java`
- Estratégia para hidrometros de Pessoa Jurídica
- Remove extensões de arquivo do SHA
- Implementação alternativa de processamento

---

## Padrões Estruturais

### 🔌 **Adapter (Adaptador)**

**Descrição:** Converte a interface de uma classe em outra interface esperada pelos clientes. O Adapter permite que classes com interfaces incompatíveis trabalhem juntas. Funciona como um "tradutor" que adapta uma interface para outra.

**Localização:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/adapter/OCRAdapter.java`

**Propósito:**
- Adapta múltiplas estratégias OCR (ProprietarioStrategy e ColaboradorStrategy) em uma interface unificada
- Fornece métodos públicos que encapsulam a lógica de seleção de estratégia
- Atua como intermediário entre ImagemWatcher/LeituraHidrometroService e as estratégias específicas

**Estrutura:**
```java
OCRAdapter (Adapter)
│
├── proprietarioStrategy: ProprietarioStrategy  ← Strategy 1
├── colaboradorStrategy: ColaboradorStrategy    ← Strategy 2
│
└── Métodos Públicos:
    ├── extractMeterId(String filename): String
    │   └── Extrai ID do hidrometro do nome do arquivo
    │
    ├── tryExtractMeterValue(BufferedImage image): Long
    │   └── Tenta extrair valor com ambas estratégias
    │       ├── Tenta ProprietarioStrategy
    │       └── Se falhar, tenta ColaboradorStrategy
    │
    └── determineMeterType(BufferedImage image): String
        └── Determina tipo de hidrometro
            ├── Verifica ProprietarioStrategy
            └── Fallback para ColaboradorStrategy
```

**Exemplo de Uso:**
```java
@Component
public class OCRAdapter {
    
    private final ProprietarioStrategy proprietarioStrategy;
    private final ColaboradorStrategy colaboradorStrategy;
    
    // Extrai ID através do adaptador
    public String extractMeterId(String filename) {
        return proprietarioStrategy.extractMeterId(filename);
    }
    
    // Tenta ambas estratégias em sequência
    public Long tryExtractMeterValue(BufferedImage image) {
        Long proprietarioValue = proprietarioStrategy.extractMeterValue(image);
        if (proprietarioValue != null) {
            return proprietarioValue;  // Sucesso na primeira estratégia
        }
        
        Long colaboradorValue = colaboradorStrategy.extractMeterValue(image);
        return (colaboradorValue != null) ? colaboradorValue : null;  // Tenta segunda
    }
    
    // Determina tipo testando ambas estratégias
    public String determineMeterType(BufferedImage image) {
        if (proprietarioStrategy.extractMeterValue(image) != null) {
            return proprietarioStrategy.getMeterType();
        }
        if (colaboradorStrategy.extractMeterValue(image) != null) {
            return colaboradorStrategy.getMeterType();
        }
        return "DESCONHECIDO";
    }
}
```

**Fluxo de Funcionamento:**
```
ImagemWatcher detecta novo arquivo
        ↓
    OCRAdapter.extractMeterId()
        ↓
    OCRAdapter.tryExtractMeterValue()  ← Tenta estratégias em sequência
        ├── ProprietarioStrategy (1ª tentativa)
        └── ColaboradorStrategy (2ª tentativa)
        ↓
    OCRAdapter.determineMeterType()
        ├── Classifica como PROPRIETARIO ou COLABORADOR
        └── Ou retorna DESCONHECIDO
        ↓
    LeituraHidrometroService registra resultado
```

**Vantagens:**
- **Encapsulamento:** Lógica de seleção de estratégia centralizada em um único componente
- **Flexibilidade:** Fácil adicionar novas estratégias sem alterar código cliente
- **Reutilização:** Interface unificada para qualquer consumidor de OCR
- **Fallback automático:** Tenta múltiplas estratégias sem precisar conhecer detalhes
- **Desacoplamento:** ImagemWatcher/LeituraHidrometroService não conhecem estratégias específicas

**Relação com Outros Padrões:**
- ↔️ **Strategy Pattern:** OCRAdapter adapta múltiplas implementações de OcrStrategy
- ↔️ **Observer Pattern:** Utilizado por ImagemWatcher ao processar imagens detectadas
- ↔️ **Facade Pattern:** Integrado em PainelCagepaFacade para processamento OCR

**Vantagens:**
- Flexibilidade algoritmica em tempo de execução
- Fácil adição de novas estratégias
- Isolamento de lógica complexa de OCR

---

### 🏭 **Observer (Observador)**

**Descrição:** Define uma dependência um-para-muitos entre objetos para que quando um objeto mudar de estado, todos os seus dependentes sejam notificados automaticamente.

**Duas Implementações:**

#### 1️⃣ **Observer do Sistema de Arquivos (Monitoramento de Imagens)**

**Localização:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/watcher/ImagemWatcher.java`

**Componentes:**
- **Subject (Observable):** Sistema de arquivos (WatchService do Java NIO)
- **Observer:** `ImagemWatcher` - Monitora mudanças no diretório

**Funcionamento:**
```
Arquivo adicionado/modificado em diretório
                    ↓
    WatchService detecta evento ENTRY_CREATE/ENTRY_MODIFY
                    ↓
        ImagemWatcher notificado
                    ↓
  LeituraHidrometroService.processarImagemHidrometro()
                    ↓
  Processa OCR e salva leitura no banco de dados
```

**Método Principal:**
```java
public void run() {
    WatchKey key;
    while ((key = watchService.poll()) != null) {
        for (WatchEvent<?> event : key.pollEvents()) {
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE ||
                event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                processImageFile(file);  // Notifica observador
            }
        }
    }
}
```

**Vantagens:**
- Monitoramento automático de mudanças de arquivos
- Desacoplamento entre produtor (sistema de arquivos) e consumidor (processamento OCR)
- Ciclo de vida gerenciado por daemon thread

---

#### 2️⃣ **Observer de Auditoria (Rastreamento de Operações)**

**Localização:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/auditoria/`

**Componentes:**
- **Subject (Observable):** `AuditoriaService` - Gerencia observers de auditoria
- **Observer Interface:** `AuditoriaObserver` - Define contrato para notificações
- **Concrete Observers:**
  1. `LogAuditoriaObserver` - Registra operações em console com emojis
  2. `AlertaAuditoriaObserver` - Alertas para operações críticas

**Estrutura:**
```
AuditoriaService (Subject)
    │
    ├── registerObserver(AuditoriaObserver)
    ├── removeObserver(AuditoriaObserver)
    ├── notifyObservers(AuditoriaOperacao)
    │
    └── Observers Registrados:
        ├── LogAuditoriaObserver
        │   ├── operacaoRegistrada()     → Console
        │   ├── operacaoCritica()        → Console com alerta
        │   └── operacaoFalhou()         → Console com erro
        │
        └── AlertaAuditoriaObserver
            ├── operacaoRegistrada()     → Silencioso
            ├── operacaoCritica()           → Alerta crítico
            └── operacaoFalhou()           → Alerta de falha
```

**Funcionamento:**
```
Operação CRUD executada em PainelCagepaFacade
                    ↓
        Try-catch registra sucesso/falha
                    ↓
    AuditoriaService.registrarCriacaoEntidade() ou registrarFalha()
                    ↓
  Salva AuditoriaOperacao no banco de dados
                    ↓
  notifyObservers() notifica todos os observers registrados
                    ↓
    ┌─────────────────────────────────┐
    │                                 │
    ▼                                 ▼
LogAuditoriaObserver              AlertaAuditoriaObserver
(Exibe em console)                (Alerta crítico)
```

**Exemplo de Notificação:**
```java
// Quando operação é registrada
auditoriaService.registrarCriacaoEntidade(admin, "PessoaFisica", id, 
    "Criação de cliente", dadosJSON);

// Todos os observers são notificados:
// 1. LogAuditoriaObserver imprime: ✓ CREATE - PessoaFisica - SUCESSO
// 2. AlertaAuditoriaObserver: (silencioso, não é crítica)

// Quando é CONFIG_CHANGE (crítica)
auditoriaService.registrarMudancaConfiguracao(admin, "ConfiguracaoLimiteConsumo", id,
    "Desativação de notificações", dadosAntigos, dadosNovos);

// Ambos os observers são notificados:
// 1. LogAuditoriaObserver imprime: CONFIG_CHANGE - ... - SUCESSO
// 2. AlertaAuditoriaObserver imprime: ALERTA CRÍTICO: CONFIG_CHANGE ...
```

**Vantagens:**
- Encapsulamento entre lógica de negócio e auditoria
- Múltiplos observers podem reagir simultaneamente
- Fácil adicionar novos observers (ex: EmailObserver)
- Rastreamento automático sem modificar código CRUD
- Operações de auditoria não bloqueiam negócio (erros silenciados)

**Operações Auditadas:**
- ✅ CREATE - Criar Pessoa Física/Jurídica, Endereço, Hidrometro
- ✅ DELETE - Inativar Pessoa Física/Jurídica, Deletar Endereço
- ✅ UPDATE - Ativar/Desativar Hidrometro
- ✅ CONFIG_CHANGE - Configurar/Desativar/Reativar Notificações (CRÍTICAS)

---

### 📋 **Command (Comando)**

**Descrição:** Encapsula uma requisição como um objeto, permitindo que você parametrize clientes com diferentes requisições, enfileire requisições, registre requisições e suporte operações que podem ser desfeitas.

**Localização:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/command/`

**Componentes:**
- **Command Interface:** `ConsumptionCommand` - Define contrato para operações
- **Result DTO:** `ConsumptionResult` - Encapsula resultado da operação

**Estrutura:**
```java
ConsumptionCommand (Interface)
│
├── execute(): ConsumptionResult
│   └── Executa o cálculo de consumo
│
├── undo(): void
│   └── Desfaz a operação (histórico)
│
└── getDescription(): String
    └── Retorna descrição do comando
```

**Exemplo de Uso:**
```java
// Encapsula requisição como objeto
ConsumptionCommand command = new CalcularConsumoPeriodoCommand(
    shaHidrometro, 
    dataInicio, 
    dataFim
);

// Executa comando
ConsumptionResult result = command.execute();

// Desfazer se necessário
command.undo();

// Descrição para logging/auditoria
String descricao = command.getDescription();
```

**ConsumptionResult:**
```java
ConsumptionResult {
    - shaHidrometro: String           // ID do hidrometro
    - periodDescription: String       // Descrição do período
    - consumptionValue: Long          // Valor total consumido
    - dataInicio: LocalDateTime       // Data inicial
    - dataFim: LocalDateTime          // Data final
    - leituraInicial: Long            // Leitura no início
    - leituraFinal: Long              // Leitura no final
}
```

**Vantagens:**
- **Encapsulamento:** Requisições são objetos, podem ser enfileiradas
- **Undo/Redo:** Suporte a desfazer e refazer operações
- **Auditoria:** Cada comando pode ser registrado e rastreado
- **Separação de responsabilidades:** Solicitante não precisa conhecer detalhes
- **Fila de comandos:** Permite implementar filas de processamento

---

## Padrões Criacionais

### 🏗️ **Builder**

**Descrição:** Separa a construção de um objeto complexo de sua representação, permitindo que o mesmo processo de construção crie diferentes representações.

**Localização:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/config/ConfigBuilder.java`

**Estrutura:**
```
ConfigBuilder
├── withImageDirectory(String path)
└── build()  // Retorna configuração finalizada
```

**Exemplo de Uso:**
```java
ConfigBuilder builder = new ConfigBuilder();
builder
    .withImageDirectory("/home/apolo/Desktop/fachada-cagepa/imagens_hidrometros")
    .build();
```

**Vantagens:**
- Construção flexível e passo-a-passo
- Validação de configurações antes da criação
- Código legível e mantível

---

### 🎛️ **Singleton**

**Descrição:** Garante que uma classe tenha apenas uma instância e fornece um ponto de acesso global a ela.

**Localização:** `src/main/java/com/fachada/cagepa/fachada_cagepa/padroes/config/ConfigManager.java`

**Implementação:**
```java
public class ConfigManager {
    private static ConfigManager instance;
    
    private ConfigManager() {
        // Construtor privado
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    public String getConfiguration(String key) { ... }
    public void setConfiguration(String key, String value) { ... }
}
```

**Responsabilidades:**
- Gerenciar configurações globais da aplicação
- Fornecer acesso centralizado a parâmetros de sistema
- Persistir configurações

**Vantagens:**
- Acesso global a configurações
- Única instância garante consistência
- Ponto centralizado de controle

---


### Fluxo de Integração

1. **Login** → AdminProxyFachada (PROXY) valida e autentica
2. **Monitoramento inicia** → ImagemWatcher (OBSERVER) começa a monitorar arquivos
3. **Arquivo detectado** → WatchService notifica ImagemWatcher
4. **OCR processado** → LeituraHidrometroService usa OcrStrategy (STRATEGY)
5. **Auditoria automática** → Cada operação CRUD registra em AuditoriaService (OBSERVER)
6. **Notificações** → LogAuditoriaObserver e AlertaAuditoriaObserver notificados
7. **Configurações** → ConfigManager (SINGLETON) fornece dados
8. **Operações gerenciadas** → PainelCagepaFacade (FACADE) orquestra tudo

---

## 📊 Resumo de Padrões

| Padrão | Tipo | Localização | Propósito |
|--------|------|-----------|----------|
| **Facade** | Estrutural | `PainelCagepaFacade.java` | Interface unificada para subsistemas |
| **Proxy** | Estrutural | `*ProxyFachada.java` (3 classes) | Controle de acesso e validação |
| **Adapter** | Estrutural | `OCRAdapter.java` | Adapta múltiplas estratégias de OCR em interface unificada |
| **Strategy** | Comportamental | `ProprietarioStrategy.java`, `ColaboradorStrategy.java` | Algoritmos intercambiáveis de OCR |
| **Observer (Arquivos)** | Comportamental | `ImagemWatcher.java` | Monitoramento de mudanças de arquivos |
| **Observer (Auditoria)** | Comportamental | `AuditoriaService.java`, `LogAuditoriaObserver.java`, `AlertaAuditoriaObserver.java` | Rastreamento de operações críticas |
| **Command** | Comportamental | `ConsumptionCommand.java`, `ConsumptionResult.java` | Encapsula requisições como objetos, suporta undo/redo |
| **Builder** | Criacional | `ConfigBuilder.java` | Construção flexível de configurações |
| **Singleton** | Criacional | `ConfigManager.java` | Instância única de gerenciador global |

---