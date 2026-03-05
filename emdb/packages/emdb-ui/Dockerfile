FROM node:24-alpine as build-stage
WORKDIR /app

COPY package*.json ./
COPY packages/emdb-common ./packages/emdb-common
COPY packages/emdb-ui ./packages/emdb-ui

RUN npm install
WORKDIR /app/packages/emdb-common
RUN npm run build
WORKDIR /app/packages/emdb-ui
RUN npm run build

FROM nginx:1.29.4-alpine as production-stage
COPY --from=build-stage /app/packages/emdb-ui/dist /usr/share/nginx/html
COPY packages/emdb-ui/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]