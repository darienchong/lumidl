# lumidl
A tool to help download files from LumiNUS.

Much of the logic, etc. derived from Julius's [downloader](https://github.com/indocomsoft/fluminurs) which stopped working for me, thus motivating this project.

## How to use
1. Download the .jar file from [here](https://github.com/snajef/lumidl/releases).
2. Run the .jar file from your command-line with the following parameters:
```
java -Dusername={username} -Dpassword={password} -Dpath={path_to_download_files_to} -jar path\to\lumidl.jar
```
The parameters should correspond to these:
```
username = nusstu\your_NUSNET_id
password = your_NUSNET_password
path = path\to\root\downloads\folder
```
