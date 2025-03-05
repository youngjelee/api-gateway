- vpc 하위에 ,  Private DNS 네임스페이스 생성   , 이름은 msa.local로 지정 
aws servicediscovery create-private-dns-namespace --name <msa.local> --vpc <VPC_ID>



- 태스크 추가 

{
  "family": "api-gateway",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "executionRoleArn": "arn:aws:iam::<AWS_ACCOUNT_ID>:role/ecsTaskExecutionRole",
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "api-gateway",
      "image": "<AWS_ACCOUNT_ID>.dkr.ecr.ap-northeast-2.amazonaws.com/api-gateway:latest",
      "essential": true,
      "portMappings": [
        {
          "containerPort": 8080,
          "hostPort": 8080
        }
      ]
    }
  ]
}
