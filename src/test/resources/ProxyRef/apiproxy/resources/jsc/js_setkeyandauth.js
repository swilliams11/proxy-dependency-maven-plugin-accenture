client_id = context.getVariable('client_id');
client_secret = context.getVariable('client_secret');
client_secret && context.setVariable("request.header.Authorization",CryptoJS.enc.Base64.stringify(CryptoJS.enc.Latin1.parse(client_id + ':' + client_secret)));
context.setVariable('grant_type','client_credentials');












