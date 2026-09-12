FROM node:24-alpine as build-stage
WORKDIR /app

COPY package*.json ./
COPY emdb-ui ./emdb-ui

WORKDIR /app/emdb-ui
RUN npm run build

FROM nginx:1.29.4-alpine as production-stage
COPY --from=build-stage /app/emdb-ui/dist /usr/share/nginx/html
COPY emdb-ui/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]