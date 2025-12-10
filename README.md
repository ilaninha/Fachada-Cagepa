# 💧 Sistema Inteligente de Monitoramento de Hidrômetros (SHA - CAGEPA)

> **Versão:** 2.0.0 | **Status:** iniciado(em andamento)

Este projeto é uma solução de **Visão Computacional e Automação** desenvolvida para modernizar o processo de leitura de hidrômetros analógicos. O sistema monitora diretórios em tempo real, detecta novas imagens de medidores, extrai a leitura numérica automaticamente via OCR (Reconhecimento Óptico de Caracteres) e registra os dados para faturamento, eliminando erros de digitação humana.

---

## 🚀 Funcionalidades Principais

* **📸 Monitoramento em Tempo Real:** Utiliza a API `WatchService` do Java NIO para vigiar pastas locais. Assim que uma foto é transferida pelo leiturista, o sistema inicia o processamento instantaneamente.
* **🧠 Extração de Dígitos (OCR):** Integração com **Tesseract OCR** para ler os números nos hidrômetros analógicos com alta precisão.
* **🛡️ Auditoria e Logs:** Registro detalhado de todas as operações (sucesso, falha, tentativas de fraude) utilizando `Logback`.
* **🏗️ Arquitetura Robusta:** Desenvolvido com **Spring Boot** e estruturado sobre Padrões de Projeto (Design Patterns) clássicos.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.3.0
* **Build Tool:** Gradle
* **Visão Computacional:** Tesseract OCR / Java Advanced Imaging
* **Banco de Dados:** H2 Database (Memória/Dev) / PostgreSQL (Produção)
* **Controle de Versão:** Git & GitHub

---

## 🏛️ Padrões de Projeto (Design Patterns)

Este projeto foi desenvolvido com foco em Engenharia de Software, aplicando padrões do GoF para garantir desacoplamento e manutenibilidade:

1.  **Observer Pattern:** O `ImageWatcher` atua como *Subject*, notificando múltiplos *Observers* (serviços de OCR, Auditoria, Faturamento) sempre que uma nova imagem é detectada.
2.  **Facade Pattern:** A classe `PainelCagepaFacade` simplifica a interface do sistema, ocultando a complexidade de inicialização dos subsistemas de monitoramento e segurança.
3.  **Strategy Pattern:** Utilizado para alternar entre diferentes algoritmos de processamento de imagem (ex: `TesseractStrategy` vs `NeuralNetworkStrategy`) sem alterar o código cliente.
4.  **Factory Method:** Responsável pela criação das instâncias de processadores de imagem dependendo do tipo de arquivo (.jpg, .png).

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos
* Java JDK 21+ instalado.
* Tesseract OCR instalado no sistema operacional (e adicionado ao PATH).
* Uma pasta local criada em `C:/cagepa_imagens`.

### Passo a Passo
1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/SEU-USUARIO/fachada-painel-cagepa.git](https://github.com/SEU-USUARIO/fachada-painel-cagepa.git)
    ```
2.  **Entre na pasta do projeto:**
    ```bash
    cd fachada-painel-cagepa
    ```
3.  **Execute via Gradle:**
    ```bash
    ./gradlew bootRun
    ```
4.  **Teste:**
    * O console exibirá: `STATUS: MONITORAMENTO ATIVO`.
    * Cole uma imagem de hidrômetro na pasta `C:/cagepa_imagens`.
    * Acompanhe o log de extração e auditoria no terminal.

---

## 📂 Estrutura do Projeto

```text
src/main/java/com/fachada/cagepa
├── domain
│   ├── enterprise
│   │   ├── observer       # Implementação do Padrão Observer
│   │   └── validation     # Regras de negócio
│   ├── application
│   │   └── services       # Serviços de OCR e Auditoria
│   └── util               # Extratores de dígitos e helpers
├── facade                 # Padrão Facade (Ponto de entrada)
└── FachadaCagepaApplication.java

POR: Ilana Costa
