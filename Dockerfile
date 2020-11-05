FROM node:alpine
RUN apk add --update bash tree \
	&& rm -rf /var/cache/apk/*

RUN npm install -g yo generator-asf
RUN npm --force cache clear
RUN mkdir /yo && mkdir /conf
RUN chown -R root:root /yo \
	&& chown -R root:root /conf \
	&& chown -R root:root /usr/local/lib/node_modules
USER root

WORKDIR /yo

CMD ["asf", "create-service", "--config", "/conf/asf.json"]
