docker run \
  -e 'ACCEPT_EULA=Y' \
  -e 'MSSQL_SA_PASSWORD=MyS3cr3t@Passwort!' \
  -p 1401:1433 \
  -d mcr.microsoft.com/mssql/server:2022-latest
