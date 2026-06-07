# Stage 1: Build the React app
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve with Nginx over HTTPS
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY certs/localhost+3.pem /etc/ssl/certs/localhost+3.pem
COPY certs/localhost+3-key.pem /etc/ssl/private/localhost+3-key.pem
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 443
CMD ["nginx", "-g", "daemon off;"]
