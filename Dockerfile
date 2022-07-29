FROM ubuntu:18.04
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update -y && apt-get install openjdk-8-jdk maven tomcat8 libpq-dev postgresql-client-10 python-psycopg2 postgis curl jq -y
RUN update-java-alternatives --set java-1.8.0-openjdk-amd64

ARG DBINFO_CSV="TNAtoolAPI-Webapp/src/main/resources/admin/resources/dbInfo.template.csv"
ARG TOMCAT_USERS_XML="TNAtoolAPI-Webapp/src/main/resources/admin/resources/tomcat-users.template.xml"

ENV JAVA_OPTS="-Xmx2G"
ENV CATALINA_BASE="/var/lib/tomcat8"
ENV CATALINA_HOME="/usr/share/tomcat8"
ENV CONFDIR /app/conf

# Create tomcat, config dirs
RUN rm -rf ${CATALINA_BASE}/webapps
RUN mkdir -p ${CATALINA_BASE}/temp ${CATALINA_BASE}/webapps ${CONFDIR}/admin/resources

# Cache dependencies as a build layer
ADD ./TNAtoolAPI-Webapp/pom.xml /app/pom.xml
RUN cd /app && mvn dependency:go-offline

# Build tnext
ADD ./TNAtoolAPI-Webapp /app
RUN cd /app && mvn clean install
RUN cp /app/target/TNAtoolAPI-Webapp-0.0.1-SNAPSHOT.war ${CATALINA_BASE}/webapps/ROOT.war

# Configuration
RUN echo "edu.oregonstate.tnatool.ConfigurationDirectory = ${CONFDIR}/" >> ${CATALINA_BASE}/conf/catalina.properties
COPY ${TOMCAT_USERS_XML} ${CATALINA_BASE}/conf/tomcat-users.xml
COPY ${DBINFO_CSV} ${CONFDIR}/admin/resources/dbInfo.csv

# Default command
EXPOSE 8080
CMD ["/usr/share/tomcat8/bin/catalina.sh", "run"]
