FROM node:18.16.1 as build-stage
WORKDIR /var/jenkins_home/workspace/ShadowMate/FE
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
CMD ["npm","run","start"]