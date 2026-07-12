# 🚀 Instructions de build — INNOV'ACTION Finance

## Prérequis : GitHub Codespaces

1. Allez sur https://github.com → Nouveau dépôt → Importez ou créez
2. Poussez tout le contenu du ZIP dans le dépôt
3. Cliquez sur **Code → Codespaces → New codespace**

---

## Étape 1 — Installer Android SDK dans Codespaces

```bash
# Dans le terminal Codespaces
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-11076708_latest.zip -d $HOME/android-sdk/cmdline-tools
mv $HOME/android-sdk/cmdline-tools/cmdline-tools $HOME/android-sdk/cmdline-tools/latest

export ANDROID_HOME=$HOME/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accepter les licences et installer les outils
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0"
```

---

## Étape 2 — Configurer local.properties

```bash
cp local.properties.example local.properties

# Editez local.properties et remplissez :
sdk.dir=$HOME/android-sdk
KEYSTORE_PATH=innovaction.jks
KEYSTORE_PASSWORD=VotreMotDePasse
KEY_ALIAS=innovaction
KEY_PASSWORD=VotreMotDePasseCle
```

---

## Étape 3 — Créer le Keystore (une seule fois)

```bash
keytool -genkey -v \
  -keystore innovaction.jks \
  -alias innovaction \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -dname "CN=INNOVACTION, OU=Finance, O=ASBL, L=Kinshasa, S=Kinshasa, C=CD"
```

> ⚠️ Conservez ce fichier `.jks` et son mot de passe précieusement.
> Si vous le perdez, vous ne pourrez plus mettre à jour l'app sur le Play Store.

---

## Étape 4 — Build APK Debug (test rapide)

```bash
chmod +x gradlew
./gradlew assembleDebug

# APK disponible ici :
# app/build/outputs/apk/debug/app-debug.apk
```

---

## Étape 5 — Build APK Release (installation et Play Store)

```bash
./gradlew assembleRelease

# APK signé disponible ici :
# app/build/outputs/apk/release/app-release.apk
```

---

## Étape 6 — Installer l'APK sur votre téléphone

**Option A — Via ADB (câble USB) :**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Option B — Transfert fichier :**
1. Téléchargez le fichier `.apk` depuis Codespaces
2. Transférez-le sur votre téléphone (WhatsApp, email, Google Drive…)
3. Ouvrez le fichier sur le téléphone
4. Activez "Sources inconnues" si demandé → Installer

---

## Résolution des problèmes courants

| Erreur | Solution |
|--------|----------|
| `SDK not found` | Vérifiez `sdk.dir` dans `local.properties` |
| `License not accepted` | Relancez `yes \| sdkmanager --licenses` |
| `Keystore not found` | Vérifiez `KEYSTORE_PATH` dans `local.properties` |
| `Hilt compilation failed` | Lancez `./gradlew kspDebugKotlin` pour voir les détails |
| `Room schema error` | Supprimez `app/schemas/` et relancez |

