# Projeto Java + Spring em Cluster com Docker Swarm

Este projeto demonstra uma aplicação **Java + Spring Boot** containerizada e distribuída em **Docker Swarm**, utilizando **Nginx** como load balancer em um ambiente de **3 VMs na AWS**.

---

## 📌 Arquitetura

- **Aplicação**: Java + Spring Boot
- **Orquestração**: Docker Swarm
- **Load Balancer**: Nginx
- **Infraestrutura**: 3 VMs EC2 na AWS
- **Rede**: Overlay network para comunicação entre containers

Fluxo:
1. Usuário acessa o **Nginx** (load balancer).
2. Nginx distribui requisições entre os containers da aplicação.
3. Docker Swarm garante alta disponibilidade e escalabilidade.

---

## 🚀 Pré-requisitos

- Conta AWS com 3 instâncias EC2 (Ubuntu utilizado).
- Docker e Docker Swarm instalados em todas as VMs.
- Git para clonar o repositório. Ou, utilizar a imagem [gbrlo/user-ms:1](https://hub.docker.com/r/gbrlo/user-ms).

---

## ⚙️ Passos de Configuração

### 1. Clonar o repositório em cada VM.
```bash
git clone https://github.com/gabriel-olv/ms-aws-nginx.git
cd ms-aws-nginx
```

### 2. Gerar a imagem do app em cada VM.
```bash
docker image build -t user-ms:1 .
```

### 3. Inicializar Cluster com Docker Swarm
Considerando instalado docker e docker swarm. Execute o comando abaixo na VM principal:
```bash 
docker swarm init
```
<img src="doc/1. docker swarm init.png" width="100%"/>

Nas outras VMs, o join command:

Para isso, primeiro incluir a política de segurança na console AWS, permitindo entrada, nos IPs internos, a porta 2377/TCP.
```bash 
docker swarm join <TOKEN> <IP_VM_PRINCIAL>:2377
```
E conferir na VM principal se tudo ocorreu bem:
```bash
docker node ls
```
<img src="doc/2. worker joined.png" width="100%"/>


### 4. Criar rede overlay
Nesse momento é necessário também, incluir as políticas de segurança na console AWS, permitindo as portas:
- 7946 TCP/UDP: para descoberta dos nós na rede overlay; e
- 4789 UDP: para tráfego na rede overlay.

Conforme documentação [Docker Swarm](https://docs.docker.com/engine/swarm/swarm-tutorial/#:~:text=%3A%20192.168.99.100.-,Open%20protocols%20and%20ports%20between%20the%20hosts,-The%20following%20ports).

```bash
docker network create -d overlay my-app-net
```

### 5. Criar serviços na rede criada
```bash
# O bando de dados PostgreSQL
docker service create --name db \
--network my-app-net \ # mesmo nome da rede criada anteriormente
-e POSTGRES_PASSWORD=<DB_PASS_HERE> \
-e POSTGRES_DB=<DB_NAME_HERE> \
-p 5432:5432 \
-d postgres:18

# O nosso microsserviço
docker service create --name app \
--network my-app-net \ 
-e DB_URL=jdbc:postgresql://db:5432/<DB_NAME_HERE> \
-e DB_USER=postgres \
-e DB_PASSWORD=<DB_PASS_HERE> \
-p 8080:8080 \
--replicas 2 \ # quantidade utilizada para este estudo
gbrlo/user-ms:1 # nome da sua imagem, caso não utilizar esta
```

Listando serviços criados:
```bash
docker service ls
```
<img src="doc/3. service replicas.png" width="100%"/>

Neste momento, já será possível fazer requisições aos endpoints da aplicação. Contudo, será necessário informar a porta exposta 8080. Além disso, é possível também observar o nosso log da aplicação mostrando o funcionamento do Load balance interno do Docker Swarm. O IP do qual você fizer a requisição será aquele mostrado no log. (Esqueci de printar essa parte 🤦‍♂️ rs)

### 6. Criar o load balancer com nginx
Com base na documentação [Nginx loadbalance doc](https://nginx.org/en/docs/http/load_balancing.html), construímos o nosso ```nginx.conf```:

<img src="doc/4. nginx configuration file.png" width="100%"/>

Seguimos então os seguintes passos:

1. Colocar o ```nginx.conf``` no contexto do cluster:
```bash
docker config create nginx.conf ./nginx.conf
```

2. Criar o serviço loadbalance com nginx, chamado aqui ```nginx-lb```:
```bash
 docker service create --name nginx-lb \
-p 80:80 \
--config src=nginx.conf,target=/etc/nginx/nginx.conf \
--network my-app-net \ # mesma rede do cluster
nginx
```
<img src="doc/5. nginx service.png" width="100%"/>

Agora fazendo a requisição pelo DNS público de um VM, o nginx faz o balanceamento das requisições:

1. Algumas requisições feitas no endpoint GET /users:
<img src="doc/6. request after nginx.png" width="100%"/>

Observando o log em cada vm worker, conseguimos ver o trabalho o nginx como load balancer:

1. Na primeira VM - worker:
<img src="doc/7. request received by test-2.png" width="100%"/>

2. Na segunda VM - worker:
<img src="doc/8. request received by test-3.png" width="100%"/>

## ✅ Conclusão
Este projeto demonstrou como uma aplicação Java + Spring Boot pode ser distribuída em um cluster Docker Swarm, utilizando Nginx como load balancer em um ambiente de múltiplas VMs na AWS.

Com isso, conseguimos construir um ambiente robusto e próximo de cenários reais de produção, explorando conceitos de orquestração de containers, rede distribuída e balanceamento de carga.