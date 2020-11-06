![ic_launcher-playstore](https://user-images.githubusercontent.com/48259868/98324599-795ea500-1fcb-11eb-876b-48e54e45b70a.png)
# Passos para disponibilizar o sistema VeteriTec

## Requisitos   
    - Android Studio
    - Node JS
    - Acesso ao GitHub do sistema VeteriTec
    - Acesso ao Heroku do sistema VeteriTec
    - Usuário e senha da conta de desenvolvedor da Google Play

## Apk

    1. baixar o projeto no repositório [VeteriTec][https://github.com/cleysondiego/veteritec-mobile]
    2. Instalar as dependências do projeto através do gradlew (cli) ou pelo android studio (GUI)
    3. Gerar chave de assinatura para o APK
        - Andriod Studio > Build > Generator sign APK > Create new key
        - Adicionar diretório
        - Criar Password 
        - Preencher campos solicitados para gerar a key
        - Preencher campos solicitados para gerar o certificado
        - Avançar
        - Selecionar o tipo de build Release ou Debug
        - Marcar as opções
        - [x] v1 
        - [x] v2
        - Aguardar o build do APK
        - O Apk será gerado no diretório escolhido

 ## Google Play

    1. Acessar a página de desenvolvedor [Google Play Console][https://play.google.com/console/developers/7629223000715318863/app-list]
    2. Logar com a conta do administrador do projeto
        - Criar App
        - Preencher todos os campos solicitados
        - Acessar Versões > Produção > Criar Nova Versão 
        - Adicionar o APK ao campo solicitado
        - Aguardar a compilação da Google Play
        - Aguardar o prazo de no máximo 7 dias para análise da loja
        - Caso aprovado, o aplicativo poderá ser visualizado na loja da Google Play
        - Caso reprovado, analisar o motivo da recusa e repetir o processo

## Servidor 

    1. Copiar o link do repositório [Backend VeteriTec][https://github.com/cleysondiego/veteritec-backend]
    2. Acessar a conta da [Heroku][https://dashboard.heroku.com/]
    3. Atribuir as permissões
    4. Habilitar o CI/CD
    5. Aguardar a publicação
