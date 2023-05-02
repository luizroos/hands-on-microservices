# Criando conta na AWS

1. Acesse o site da AWS em https://aws.amazon.com/pt/
2. Clique no botão "Criar uma conta gratuita" na parte superior direita da tela
3. Preencha as informações solicitadas, como nome, sobrenome, endereço de e-mail e senha
4. Selecione o tipo de conta desejado, que pode ser Pessoal ou Empresarial
5. Preencha as informações de pagamento, caso necessário
6. Selecione o plano gratuito, que oferece recursos limitados por um período de 12 meses
7. Clique no botão "Criar uma conta e entrar no console"
8. Aguarde a confirmação de criação da conta, que será enviada para o endereço de e-mail cadastrado
9. Acesse o console da AWS com as credenciais cadastradas e comece a explorar os serviços oferecidos.

Lembrando que, mesmo que a conta seja gratuita, alguns serviços da AWS podem gerar custos, portanto, é importante estar atento aos valores cobrados e ao uso dos recursos disponíveis.


# Criando as chaves de acesso

A Access Key é usada para acessar os serviços da AWS via API ou linha de comando. As Access Keys são compostas por duas partes, uma chave de acesso (Access Key ID) e uma chave secreta (Secret Access Key). As duas partes são necessárias para autenticação, e elas são usadas para identificar e verificar a permissão de acesso do usuário aos serviços da AWS.

1. Faça login na sua conta da AWS em https://aws.amazon.com/pt/
2. Clique no seu nome de usuário na parte superior direita da tela e selecione "My Security Credentials"
3. Na aba "Access keys (chaves de acesso)", clique no botão "Create New Access Key"
4. Será exibida uma mensagem com as credenciais de acesso, incluindo o Access Key ID e o Secret Access Key. Copie essas informações e salve em um local seguro, pois serão necessárias para configurar a autenticação nos seus Terraforms.
5. É importante lembrar que o Secret Access Key é confidencial e deve ser tratado com cuidado, pois ele permite o acesso aos seus recursos na AWS. Nunca compartilhe essa informação com terceiros e, em caso de perda ou comprometimento da chave, crie uma nova imediatamente.

# Criando uma key pair

A Key Pair é usada para acessar instâncias do Amazon EC2 de forma segura, ela consiste em duas partes: uma chave pública que é armazenada no servidor e uma chave privada que é armazenada localmente. Essa chave é usada para autenticar o usuário ao se conectar a uma instância do EC2.

1. Acesse o Console da AWS e selecione o serviço "EC2".
2. Clique em "Key Pairs" no menu lateral esquerdo.
3. Clique em "Create Key Pair".
4. Digite um nome para a Key Pair e selecione o formato de arquivo que você deseja salvar a chave.
5. Clique em "Create Key Pair".
6. O arquivo de chave privada será baixado automaticamente, salve-o em um local seguro. A chave privada é necessária para acessar as instâncias EC2.