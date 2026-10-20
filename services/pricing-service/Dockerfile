# syntax=docker/dockerfile:1.7
FROM python:3.14-slim AS build
WORKDIR /src
COPY requirements.txt .
RUN pip install --no-cache-dir --prefix=/install -r requirements.txt
COPY app ./app

FROM python:3.14-slim
WORKDIR /app
RUN groupadd --gid 65532 nonroot && useradd --uid 65532 --gid 65532 --no-create-home nonroot
COPY --from=build /install /usr/local
COPY app ./app
USER 65532:65532
EXPOSE 8080
ENV PORT=8080
CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]
