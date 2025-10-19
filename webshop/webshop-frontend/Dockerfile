# Stage 1: Build the React app
FROM node:20-alpine AS build

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy all source files
COPY . .

# Build the React app
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
WORKDIR /etc/nginx

# Copy build output to Nginx html folder
COPY --from=build /app/build /usr/share/nginx/html

# Copy SSL certificate and key (PEM files)
COPY certs/localhost+1.pem /etc/ssl/certs/localhost+1.pem
COPY certs/localhost+1-key.pem /etc/ssl/private/localhost+1-key.pem

# Copy custom nginx config
COPY nginx.conf /etc/nginx/nginx.conf

# Expose HTTPS port
EXPOSE 443

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
