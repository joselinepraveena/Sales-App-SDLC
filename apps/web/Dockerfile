# syntax=docker/dockerfile:1.7
FROM node:22-alpine AS build
WORKDIR /src
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM node:22-alpine
WORKDIR /app
RUN addgroup -g 65532 nonroot && adduser -u 65532 -G nonroot -D nonroot
COPY --from=build /src/.next/standalone ./
COPY --from=build /src/.next/static ./.next/static
USER 65532:65532
EXPOSE 3000
ENV PORT=3000
CMD ["node", "server.js"]
