# Docker-Jenkins-LTS-2.107.3

Step 1 - ajust user and password

* Open file security.groovy
```bash
vim build/security.groovy
```
* modify def user / def pass
```bash
	def user = "admin"
	def pass = "jenkins"
```
Step 2 - Build imagem
```bash
docker build -f build/Dockerfile -t jenkins:1.0.0 .
```
Step 3 - Create volume jenkins
```
docker volume create --name=jenkins
```
Step 4 - Stack deploy
```bash
docker-compose up -d
```
Step 5 - Access URL
```bash
http://IP:8080
```
