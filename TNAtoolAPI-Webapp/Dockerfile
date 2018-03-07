FROM ubuntu:16.04
RUN apt-get update -y
RUN apt-get install awscli openjdk-8-jdk tomcat8 libpq-dev postgresql-client postgis libgeos-dev python-psycopg2 -y

ARG DBINFO_CSV="src/main/resources/admin/resources/dbInfo.template.csv"
ARG TOMCAT_USERS_XML="src/main/resources/admin/resources/tomcat-users.template.xml"

ENV JAVA_OPTS="-Xmx2G"
ENV CATALINA_BASE="/var/lib/tomcat8"
ENV CATALINA_HOME="/usr/share/tomcat8"

# copy application WAR (with libraries inside)
RUN rm -rf ${CATALINA_BASE}/webapps
RUN mkdir -p ${CATALINA_BASE}/webapps
COPY target/TNAtoolAPI-Webapp-0.0.1-SNAPSHOT.war ${CATALINA_BASE}/webapps/ROOT.war

# tnast config
ENV CONFDIR /app/conf
RUN mkdir -p ${CONFDIR}/admin/resources
RUN echo "edu.oregonstate.tnatool.ConfigurationDirectory = ${CONFDIR}/" >> ${CATALINA_BASE}/conf/catalina.properties
RUN echo "edu.oregonstate.tnatool.DownloadablesDirectory = ${CATALINA_BASE}/ROOT/downloadables/" >> ${CATALINA_BASE}/conf/catalina.properties
COPY ${TOMCAT_USERS_XML} ${CATALINA_BASE}/conf/tomcat-users.xml
COPY ${DBINFO_CSV} ${CONFDIR}/admin/resources/dbInfo.csv

# specify default command
EXPOSE 8080
CMD ["/usr/share/tomcat8/bin/catalina.sh", "run"]
