# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import streamlit as st
import base64
from io import BytesIO

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Streamlit WebRTC Image Capture")

    html_code = """
        <html>
        <body>
            <h1>Capture an Image</h1>
            <video id="video" width="640" height="480" autoplay></video>
            <button id="capture">Capture</button>
            <canvas id="canvas" width="640" height="480" style="display:none;"></canvas>
            <script>
                const video = document.getElementById('video');
                const canvas = document.getElementById('canvas');
                const captureButton = document.getElementById('capture');
                const context = canvas.getContext('2d');

                // Access webcam
                navigator.mediaDevices.getUserMedia({ video: true })
                    .then((stream) => {
                        video.srcObject = stream;
                    })
                    .catch((err) => {
                        console.log("Error accessing webcam: " + err);
                    });

                // Capture image on button click
                captureButton.onclick = function() {
                    context.drawImage(video, 0, 0, canvas.width, canvas.height);
                    const dataUrl = canvas.toDataURL('image/png');
                    window.parent.postMessage(dataUrl, '*');
                };
            </script>
        </body>
        </html>
    """

    # Display the HTML and capture the image
    st.components.v1.html(html_code, height=600)

    # Wait for a captured image and display it
    image_data = st.query_params.get("image", [None])[0]
    if image_data:
        # Convert the image from base64 string to image
        img = BytesIO(base64.b64decode(image_data.split(",")[1]))
        st.image(img, caption="Captured Image", use_column_width=True)


# See PyCharm help at https://www.jetbrains.com/help/pycharm/
