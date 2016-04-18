@echo off
@rem #############################################

set onf=resources\conf\blazeloader.onf
set mcp_version=snapshot\20160204
set mc_version=1.8.9

@rem #############################################

set DIR=%~dp0
set jar=%userprofile%\.gradle\caches\minecraft\net\minecraft\minecraft\%mc_version%\%mcp_version%\minecraftSrc-%mc_version%.jar

jarjar "%DIR%%onf%" "%jar%" MCP 1