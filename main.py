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
            <h1>Capture Image Using Your Phone Camera</h1>
            <video id="video" width="640" height="480" autoplay></video>
            <button id="capture">Capture</button>
            <canvas id="canvas" width="640" height="480" style="display:none;"></canvas>
            <script>
                const video = document.getElementById('video');
                const canvas = document.getElementById('canvas');
                const captureButton = document.getElementById('capture');
                const context = canvas.getContext('2d');

                // Access the phone's camera
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

                    // Send image data back to Streamlit using URL query params
                    const params = new URLSearchParams(window.location.search);
                    params.set('image', dataUrl);
                    window.history.replaceState(null, '', '?' + params.toString());
                };
            </script>
        </body>
        </html>
    """

    # Embed the HTML into Streamlit
    st.components.v1.html(html_code, height=600)

    # Fetch the image data from query parameters
    image_data = st.experimental_get_query_params().get("image", [None])[0]

    if image_data:
        # Decode base64 data and display the image
        img = BytesIO(base64.b64decode(image_data.split(",")[1]))
        st.image(img, caption="Captured Image", use_column_width=True)
    else:
        st.write("No image captured yet.")

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
