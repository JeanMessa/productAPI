<h3>Descrição</h3>
O ProductAPI é uma API REST feita com Spring Boot para registro de produtos, utilizando de tokens JWT para autenticação com endpoints de login e registro.

<h3>Dependências</h3>
Esse projeto utiliza de Java 17 com Spring Boot 3.5, Spring Boot Dev Tools, Spring Web, Spring Data JPA, Spring Security, Java JWT, Flyway e lombok. Utilizando de PostegreSQL 17.4 para o banco de dados.

<h3>Documentação</h3>
A documentação de todos os endpoints se encontra em https://documenter.getpostman.com/view/45420446/2sB2qi8cwd.
<h3>Como Testar</h3>
Teste utilizando https://documenter.getpostman.com/view/45420446/2sB2qi8cwd, através do Postman utilizando a URL do projeto no Render, ou teste localmente seguindo o passo a passo:
<ol>
  <li>Clone usando: 
    
      git clone https://github.com/JeanMessa/productAPI.git
  </li>
  <li>Instale o <a href="https://www.postgresql.org/download/
">PostgreSQL</a>. 
  </li>
  <li>Crie o banco de dados:

    CREATE DATABASE productapi;
  </li>
  <li>Configure o usuário e senha do PostgreSQL:
    <ul>
      <li>
        Abra o arquivo em productAPI/src/main/resources
/application.properties.
      </li>
      <li>
        Troque as propriedades spring.datasource.username e spring.datasource.password de acordo com o usuário e senha configuradas no seu PostgreSQL.
      </li>
    </ul>
  </li>
  <li>
    Execute a aplicação.
  </li>
  <li>
    Teste através do Postman com https://documenter.getpostman.com/view/45420446/2sB2qi8cwd utilizando a URL do localhost.
  </li>
</ol>

<h3>Endpoints</h3>
<table>
  <tr>
    <th>Método</th>
    <th>URL</th>
    <th>Descrição</th>
    <th>Nível de Usuário</th>
  </tr>
  <tr>
    <td>POST</td>
    <td>/product</td>
    <td>Endpoint para criar produtos</td>
    <td>Admin</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/product/{productId}</td>
    <td>Endpoint para recuperar produtos específicos através do Id</td>
    <td>Qualquer</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/product</td>
    <td>Endpoint para recuperar todos os produtos, podendo utilizar dos filtros name, minPrice e maxPrice</td>
    <td>Qualquer</td>
  </tr>
  <tr>
    <td>PUT</td>
    <td>/product/{productId}</td>
    <td>Endpoint para atualizar produtos</td>
    <td>Admin</td>
  </tr>
  <tr>
    <td>DELETE</td>
    <td>/product/{productId}</td>
    <td>Endpoint para excluir produtos</td>
    <td>Admin</td>
  </tr>
  <tr>
    <td>POST</td>
    <td>/user/register</td>
    <td>Endpoint para registrar novos usuários</td>
    <td>Admin</td>
  </tr>
  <tr>
    <td>POST</td>
    <td>/user/login</td>
    <td>Endpoint para conectar em uma conta de usuário e receber um token</td>
    <td>Nenhum</td>
  </tr>
  
</table>
