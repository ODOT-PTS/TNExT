FROM ubuntu:18.04
RUN apt-get update -y

# turn off build-breaking prompt from tzdata configuration
ARG DEBIAN_FRONTEND=noninteractive

# RUN apt-get install awscli openjdk-8-jdk maven tomcat8 libpq-dev postgresql-client-9.6 postgis libgeos-dev python-psycopg2 -y
RUN apt-get install awscli openjdk-8-jdk maven tomcat8 libpq-dev postgresql-client-10 postgis libgeos-dev python-psycopg2 -y

ARG DBINFO_CSV="TNAtoolAPI-Webapp/src/main/resources/admin/resources/dbInfo.template.csv"
ARG TOMCAT_USERS_XML="TNAtoolAPI-Webapp/src/main/resources/admin/resources/tomcat-users.template.xml"

ENV JAVA_OPTS="-Xmx2G"
ENV CATALINA_BASE="/var/lib/tomcat8"
ENV CATALINA_HOME="/usr/share/tomcat8"

# create a temp dir for Tomcat
RUN mkdir -p ${CATALINA_BASE}/temp

# build and copy application WAR (with libraries inside)
RUN rm -rf ${CATALINA_BASE}/webapps
RUN mkdir -p ${CATALINA_BASE}/webapps
ADD ./TNAtoolAPI-Webapp /app
RUN cd /app && mvn clean install
RUN cp /app/target/TNAtoolAPI-Webapp-0.0.1-SNAPSHOT.war ${CATALINA_BASE}/webapps/ROOT.war

# tnast config
ENV CONFDIR /app/conf
RUN mkdir -p ${CONFDIR}/admin/resources
RUN echo "edu.oregonstate.tnatool.ConfigurationDirectory = ${CONFDIR}/" >> ${CATALINA_BASE}/conf/catalina.properties
COPY ${TOMCAT_USERS_XML} ${CATALINA_BASE}/conf/tomcat-users.xml
COPY ${DBINFO_CSV} ${CONFDIR}/admin/resources/dbInfo.csv

# specify default command
EXPOSE 8080
CMD ["/usr/share/tomcat8/bin/catalina.sh", "run"]
