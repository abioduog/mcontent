FROM tomcat:8-jre8-alpine

RUN rm -rf webapps/*
COPY ./target/mContent-*.war ROOT.war

RUN mkdir webapps/ROOT && unzip ROOT.war -d webapps/ROOT && rm ROOT.war

ENV STORAGE_PATH=/storage

VOLUME /storage