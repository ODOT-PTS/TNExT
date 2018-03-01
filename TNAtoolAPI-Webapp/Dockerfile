FROM ubuntu:16.04
RUN apt-get update -y
RUN apt-get install awscli openjdk-8-jdk tomcat8 libpq-dev postgresql-client postgis libgeos-dev python-psycopg2 -y

ENV JAVA_OPTS="-Xmx2G"
ENV CATALINA_BASE=/var/lib/tomcat8
ENV CATALINA_HOME=/usr/share/tomcat8

# tnast config
WORKDIR /app
RUN mkdir -p /app/conf
RUN echo "edu.oregonstate.tnatool.ConfigurationDirectory = /app/conf/" >> ${CATALINA_BASE}/conf/catalina.properties
RUN echo "edu.oregonstate.tnatool.DownloadablesDirectory = /var/lib/tomcat8/webapps/ROOT/downloadables/" >> ${CATALINA_BASE}/conf/catalina.properties

# copy application WAR (with libraries inside)
RUN rm -rf ${CATALINA_BASE}/webapps
RUN mkdir -p ${CATALINA_BASE}/webapps
COPY target/TNAtoolAPI-Webapp-0.0.1-SNAPSHOT.war ${CATALINA_BASE}/webapps/ROOT.war

# specify default command
EXPOSE 8080
CMD ["/usr/share/tomcat8/bin/catalina.sh", "run"]
