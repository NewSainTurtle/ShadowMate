FROM node:18.16.1 as build-stage
WORKDIR /var/jenkins_home/workspace/ShadowMate/FE
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
CMD ["npm","run","start"]

FROM nginx
RUN rm -rf /etc/nginx/conf.d/defalut.conf
COPY ./nginx.conf /etc/nginx/conf.d/nginx.conf
COPY --from=build-stage /var/jenkins_home/workspace/TurtleProject/FE/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g","daemon off;"]