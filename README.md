# LuckSecure
*This is a Velocity and Paper plugin. Depends on [LuckPerms](https://github.com/LuckPerms/LuckPerms)*

*LuckSecure V1 for bungeecord [here](https://github.com/PumpMyKins/LuckSecure/tree/v1)*

![GitHub Workflow Status (with branch)](https://img.shields.io/github/actions/workflow/status/PumpMyKins/LuckSecure/maven.yml?branch=main)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/PumpMyKins/LuckSecure)
![GitHub](https://img.shields.io/github/license/PumpMyKins/LuckSecure)

#### This plugin aims to increase security by adding a custom luckperms context named `lucksecure` . A user with a group/permission in need of the `lucksecure=authenticated` context will have to authenticate himself with a time-based one-time password (Google Authenticator/Microsoft Authenticator & etc...).

---

### Ex. how to use :


1. Add permission to a user with the `lucksecure=authenticated` context (ex: `lpb user {your pseudo} permission set lsauth.cmd true lucksecure=authenticated` )

2. Test the command `lsauth-status` -> You do not have permission to execute this command!

3. As you have now a permission which needs the `lucksecure=authenticated` context to be active, you'll have to setup your TOTP. Use `/lsauth` to generate a TOTP secret/QRCODE and import it in your favortie 2AF/TOTP app.

4. Use `/lsauth {your TOTP code}` to have the `lucksecure=authenticated` context and gained your permission. 

5. Test again the command `lsauth-status {your pseudo}` -> {your pseudo} TOTP OK. You will have the `lucksecure=authenticated` context until disconnection.

### Commands :

Command name | Permission | Description
--- | --- | ---
lsauth | none | On the first use, it will generate a totp key and qrcode
lsauth {code} | none | Use to authenticate yourself with your TOTP code whe you joined the server, set the lucksecure context to `authenticated`
lsauth-status {player} | lsauth.cmd | Display the authentication status
lsauth-reset {player} | none | Reset the totp key of a player (Usable in the bungeecord console only)

### Context :

KEY | Value | Description
--- | --- | ---
lucksecure | not-authenticated | By default
lucksecure | authenticated | After a valid authentication with lsauth

### How to build :
```
git clone https://github.com/PumpMyKins/LuckSecure.git
cd LuckSecure
mvn package
```
