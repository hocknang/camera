# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import streamlit as st

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Barcode Scanner with Streamlit")

    # HTML and JavaScript code
    html_code = """
    <!DOCTYPE html>
    <html>
    <head>
      <title>Barcode Scanner</title>
      <script src="https://unpkg.com/html5-qrcode/minified/html5-qrcode.min.js"></script>
      <style>
        #qr-reader {
          width: 500px;
          margin: 20px auto;
        }
        #controls {
          display: flex;
          justify-content: center;
          gap: 10px;
          margin-top: 10px;
        }
        #qr-reader-results {
          text-align: center;
          margin-top: 20px;
          font-size: 18px;
        }
      </style>
    </head>
    <body>
      <h1 style="text-align: center;">Barcode Scanner</h1>
      <div id="controls">
        <select id="camera-select"></select>
        <button id="start-scanner" disabled>Start Scanner</button>
      </div>
      <div id="qr-reader"></div>
      <div id="qr-reader-results">Scanned Code will appear here.</div>

      <script>
        document.addEventListener("DOMContentLoaded", () => {
          const cameraSelect = document.getElementById("camera-select");
          const startButton = document.getElementById("start-scanner");
          const resultContainer = document.getElementById("qr-reader-results");
          let html5QrCode = null;

          // Fetch available cameras
          Html5Qrcode.getCameras().then(devices => {
            if (devices && devices.length) {
              devices.forEach(device => {
                const option = document.createElement("option");
                option.value = device.id;
                option.text = device.label || `Camera ${cameraSelect.length + 1}`;
                cameraSelect.appendChild(option);
              });

              startButton.disabled = false; // Enable the start button
            } else {
              resultContainer.innerHTML = "No cameras found!";
            }
          }).catch(err => {
            console.error("Error getting cameras:", err);
            resultContainer.innerHTML = "Error accessing cameras. Please check permissions.";
          });

          // Start scanning when the button is clicked
          startButton.addEventListener("click", () => {
            const selectedCameraId = cameraSelect.value;

            if (html5QrCode) {
              html5QrCode.stop().catch(err => console.error("Error stopping scanner:", err));
            }

            html5QrCode = new Html5Qrcode("qr-reader");

            const qrCodeSuccessCallback = (decodedText, decodedResult) => {
              console.log(`Code matched = ${decodedText}`, decodedResult);
              resultContainer.innerHTML = `Scanned Code: ${decodedText}`;
            };

            const config = { fps: 10, qrbox: { width: 250, height: 250 } };

            html5QrCode.start(
              selectedCameraId,
              config,
              qrCodeSuccessCallback
            ).catch(err => {
              console.error(`Error starting scanner: ${err}`);
              resultContainer.innerHTML = "Error starting scanner. Please try again.";
            });
          });
        });
      </script>
    </body>
    </html>
    """

    # Embed the HTML code in the Streamlit app
    st.components.v1.html(html_code, height=700, width=700)

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
