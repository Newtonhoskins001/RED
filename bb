name: Clon

on:
  push:
  workflow_dispatch:

jobs:
  build:
    runs-on: macos-latest
    steps:
      - name: Download ngrok
        run: curl -o ngrok.zip https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-darwin-amd64.zip

      - name: Extract ngrok
        run: unzip ngrok.zip

      - name: Authenticate ngrok
        run: ./ngrok authtoken $NGROK_AUTH_TOKEN
        env:
          NGROK_AUTH_TOKEN: ${{ secrets.NGROK_AUTH_TOKEN }}

      - name: Enable Screen Sharing (VNC)
        run: |
          sudo /System/Library/CoreServices/RemoteManagement/ARDAgent.app/Contents/Resources/kickstart \
            -activate -configure -access -on -users runner -privs -all \
            -restart -agent -menu

      - name: Set VNC password
        run: |
          echo "Setting VNC password..."
          /usr/bin/security add-generic-password -a "VNC Password" -s com.apple.VNCSettings -w "P@ssw0rd" -U
        # Note: Setting VNC password on GitHub-hosted macOS may not persist properly

      - name: Start ngrok tunnel for VNC (port 5900)
        run: ./ngrok tcp 5900
