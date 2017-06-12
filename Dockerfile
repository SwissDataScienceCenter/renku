FROM node:6.11

COPY swagger-ui/dist /opt/swagger-ui/dist
COPY lib /opt/lib
COPY package.json /opt/package.json
COPY index.html /opt/index.html
COPY src /opt/src

WORKDIR /opt

RUN npm install

CMD npm start
