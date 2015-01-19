FROM jboss/wildfly:8.2.0.Final
RUN /opt/jboss/wildfly/bin/add-user.sh -up mgmt-users.properties admin Admin#70365 --silent

#Create the AsciidoctorJ module
RUN mkdir -p ${JBOSS_HOME}/modules/org/asciidoctor/main

#Set the ASCIIDOCTORJ_MODULE env variable
ENV ASCIIDOCTORJ_MODULE /opt/jboss/wildfly/modules/org/asciidoctor/main

#Set the ULR_BASE en variable
ENV URL_BASE https://repo1.maven.org/maven2/

ADD module.xml ${ASCIIDOCTORJ_MODULE}/module.xml
RUN cd ${ASCIIDOCTORJ_MODULE} \
&& curl -O ${URL_BASE}org/asciidoctor/asciidoctorj/1.5.2/asciidoctorj-1.5.2.jar \
&& curl -O -m 900 ${URL_BASE}org/jruby/jruby-complete/1.7.16.1/jruby-complete-1.7.16.1.jar

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
