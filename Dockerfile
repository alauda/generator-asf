FROM node:alpine
ARG generator_asf_version
RUN apk add --update bash tree \
	&& rm -rf /var/cache/apk/*

RUN npm install -g yo generator-asf@$generator_asf_version
RUN npm --force cache clear
RUN mkdir /yo && mkdir /conf
RUN chown -R root:root /yo \
	&& chown -R root:root /conf \
	&& chown -R root:root /usr/local/lib/node_modules
USER root

WORKDIR /yo

CMD ["asf","cs", "--config", "/conf/asf.json"]
