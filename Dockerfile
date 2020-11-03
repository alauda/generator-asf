FROM node:alpine
RUN apk add --update bash tree \
	&& rm -rf /var/cache/apk/*

RUN npm install -g yo generator-asf
RUN npm --force cache clear
RUN mkdir /yo && mkdir /conf
RUN chown -R node:node /yo \
	&& chown -R node:node /conf \
	&& chown -R node:node /usr/local/lib/node_modules
USER node

WORKDIR /yo

CMD ["asf", "create-service", "--config", "/conf/asf.json"]