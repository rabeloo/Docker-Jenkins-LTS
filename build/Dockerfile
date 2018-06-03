# imagem default
FROM openjdk:8-jdk

RUN apt-get update && apt-get install -y vim-tiny jq git curl zip unzip iputils-ping telnet net-tools apt-transport-https ca-certificates curl python-pip dirmngr sudo libltdl7 && \
    rm -rf /var/lib/apt/lists/*

ENV VAULT_VERSION 0.10.0
ENV TERRAFORM_VERSION 0.11.7

ENV TINI_VERSION 0.14.0
ENV TINI_SHA 6c41ec7d33e857d4779f14d9c74924cab0c7973485d2972419a3b7c7620ff5fd

ENV JENKINS_HOME /var/jenkins_home
ENV JENKINS_SLAVE_AGENT_PORT ${agent_port}
ENV JENKINS_VERSION ${JENKINS_VERSION:-2.107.3}

ARG JENKINS_VERSION
ARG JENKINS_SHA=17a9e509bec5b16bde5b50bc7f59d5f1e458a55fe433deb86fd73b865bf89ab8
ARG JENKINS_URL=https://repo.jenkins-ci.org/public/org/jenkins-ci/main/jenkins-war/${JENKINS_VERSION}/jenkins-war-${JENKINS_VERSION}.war

ENV JENKINS_UC https://updates.jenkins.io
ENV JENKINS_UC_EXPERIMENTAL=https://updates.jenkins.io/experimental

ENV COPY_REFERENCE_FILE_LOG $JENKINS_HOME/copy_reference_file.log
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000
ARG http_port=8080
ARG agent_port=50000

RUN groupadd -g ${gid} ${group} && useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

RUN wget https://releases.hashicorp.com/vault/${VAULT_VERSION}/vault_${VAULT_VERSION}_linux_amd64.zip && \
    unzip vault_${VAULT_VERSION}_linux_amd64.zip && \
    mv vault /usr/bin/ && \
    wget https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip && \
    unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip && \
    mv terraform /usr/bin && \
    rm /etc/localtime && \
    ln -s /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    pip install ansible

COPY build/91-jenkins /etc/sudoers.d/

VOLUME /var/jenkins_home

RUN mkdir -p /usr/share/jenkins/ref/init.groovy.d && \
    curl -fsSL https://github.com/krallin/tini/releases/download/v${TINI_VERSION}/tini-static-amd64 -o /bin/tini && \
    chmod +x /bin/tini && \
    echo "$TINI_SHA  /bin/tini" | sha256sum -c -

COPY build/init.groovy /usr/share/jenkins/ref/init.groovy.d/tcp-slave-agent-port.groovy

RUN curl -fsSL ${JENKINS_URL} -o /usr/share/jenkins/jenkins.war && \ 
    echo "${JENKINS_SHA} /usr/share/jenkins/jenkins.war" | sha256sum -c - && \
    chown -R ${user} "$JENKINS_HOME" /usr/share/jenkins/ref

EXPOSE ${http_port}
EXPOSE ${agent_port}
USER ${user}

COPY build/jenkins-support /usr/local/bin/jenkins-support
COPY build/jenkins.sh /usr/local/bin/jenkins.sh

ENTRYPOINT ["/bin/tini", "--", "/usr/local/bin/jenkins.sh"]

COPY build/plugins.sh /usr/local/bin/plugins.sh
COPY build/install-plugins.sh /usr/local/bin/install-plugins.sh
COPY build/security.groovy /usr/share/jenkins/ref/init.groovy.d/security.groovy
COPY build/plugins.txt /usr/share/jenkins/ref/plugins.txt

RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
