{{- define "sales-service.name" -}}
{{- default .Chart.Name .Values.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "sales-service.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := include "sales-service.name" . -}}
{{- printf "%s" $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "sales-service.labels" -}}
app.kubernetes.io/name: {{ include "sales-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: sales-platform
sales.example/owner: {{ .Values.labels.owner | quote }}
sales.example/cost-center: {{ .Values.labels.costCenter | quote }}
sales.example/data-classification: {{ .Values.labels.dataClassification | quote }}
sales.example/environment: {{ .Values.labels.environment | quote }}
{{- end -}}

{{- define "sales-service.selectorLabels" -}}
app.kubernetes.io/name: {{ include "sales-service.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- define "sales-service.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "sales-service.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}

{{- define "sales-service.image" -}}
{{- if .Values.image.digest -}}
{{- printf "%s/%s@%s" .Values.image.registry .Values.image.repository .Values.image.digest -}}
{{- else -}}
{{- printf "%s/%s:%s" .Values.image.registry .Values.image.repository .Values.image.tag -}}
{{- end -}}
{{- end -}}
