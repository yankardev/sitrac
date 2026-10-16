# Compila Angular y lo sirve con Nginx. Nginx reenvía /api/... a cada microservicio,
# igual que proxy.conf.json en desarrollo.
FROM node:24-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npx ng build --configuration production

FROM nginx:1.27-alpine
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/sitrac-frontend/browser /usr/share/nginx/html
EXPOSE 80
