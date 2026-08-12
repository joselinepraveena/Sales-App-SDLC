# syntax=docker/dockerfile:1.7
FROM node:26-alpine AS build
WORKDIR /src
COPY package.json ./
RUN npm install --omit=dev=false
COPY tsconfig.json ./
COPY src ./src
RUN npm run build

FROM node:26-alpine
WORKDIR /app
RUN addgroup -g 65532 nonroot && adduser -u 65532 -G nonroot -D nonroot
COPY --from=build /src/package.json ./
COPY --from=build /src/node_modules ./node_modules
COPY --from=build /src/dist ./dist
USER 65532:65532
EXPOSE 8080
CMD ["node", "dist/main.js"]
