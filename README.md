# Desafio Técnico - Montreal

## Requisitos

Antes de começar, certifique-se de ter instalado:
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Passo a Passo para Executar

### 1. Clonar o repositório
```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
```

### 2. Subir os containers
```bash
docker compose up -d
```

Este comando irá iniciar:
- **db** (MySQL)
- **backend** (Spring Boot)
- **frontend** (Vue.js)

### 3. Acessar a aplicação
- Frontend: [http://localhost:5173](http://localhost:5173)
- Backend API: [http://localhost:8080](http://localhost:8080)

### 4. Encerrar a aplicação
Para parar todos os containers:
```bash
docker compose down
```

## Estrutura do Projeto

```
.
├── backend/      # Código fonte do Spring Boot
├── frontend/     # Código fonte do Vue.js
├── docker-compose.yml
└── README.md
```

## Observações

- O banco de dados MySQL será iniciado com a base `desafio` já criada.
- O backend está configurado para se conectar automaticamente ao banco.
- Certifique-se de que as portas 3306, 8080 e 5173 estejam livres.
