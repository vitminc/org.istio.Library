#
openssl req -nodes -new -x509 -keyout /export/gateway-keys/server.key -out /export/gateway-keys/server.cert -subj "/CN=gateway.test.com"


istioctl manifest generate --set values.gateways.istio-ingressgateway.sds.enabled=true > istio-ingressgateway.yaml

kubectl create -n istio-system secret generic library-credential --from-file=key=server.key --from-file=cert=server.cert