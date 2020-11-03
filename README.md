# generator-asf [![NPM version][npm-image]][npm-url] [![Build Status][travis-image]][travis-url] [![Coverage percentage][coveralls-image]][coveralls-url]
> 

## Installation 

install [Yeoman](http://yeoman.io) and generator-asf using [npm](https://www.npmjs.com/) (we assume you have pre-installed [node.js](https://nodejs.org/)).

```bash
npm install -g yo
npm install -g generator-asf
```

Then generate your new microservice:

```bash
asf create-service
```

Or generate your new gateway:

```bash
asf create-gateway
```

## Persistent configuration

```bash
asf create-service -c asf.json
```
File asf.conf will be generated with all options.
Execute the same command will run with options in it.

## Run in docker

generate microservice:

```bash
docker run --rm -it \
	-v $PWD:/yo dwgao/generator-asf \
	asf create-service
```
generate gateway:

```bash
docker run --rm -it \
	-v $PWD:/yo dwgao/generator-gateway \
	asf create-gateway
```
with persistent configuration:

```bash
docker run --rm -it \
	-v ~/conf:/conf \
	-v $PWD:/yo dwgao/generator-asf \
	asf create-service -c /conf/asf.conf
```

## Getting To Know Yeoman

 * Yeoman has a heart of gold.
 * Yeoman is a person with feelings and opinions, but is very easy to work with.
 * Yeoman can be too opinionated at times but is easily convinced not to be.
 * Feel free to [learn more about Yeoman](http://yeoman.io/).

## License

Apache-2.0 © [gaodawei]()


[npm-image]: https://badge.fury.io/js/generator-asf.svg
[npm-url]: https://npmjs.org/package/generator-asf
[travis-image]: https://travis-ci.com/madogao/generator-asf.svg?branch=master
[travis-url]: https://travis-ci.com/madogao/generator-asf
[daviddm-image]: https://david-dm.org/madogao/generator-asf.svg?theme=shields.io
[daviddm-url]: https://david-dm.org/madogao/generator-asf
[coveralls-image]: https://coveralls.io/repos/madogao/generator-asf/badge.svg
[coveralls-url]: https://coveralls.io/r/madogao/generator-asf
