# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.

import streamlit as st
import cv2
import numpy as np

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Camera Capture and Upload")

    image = np.zeros((100, 100, 3), dtype="uint8")

    # Draw a rectangle
    cv2.rectangle(image, (10, 10), (90, 90), (0, 255, 0), 2)

    # Save the image to verify OpenCV works
    cv2.imwrite("test_image.jpg", image)

    st.write("Image created and saved as test_image.jpg")



# See PyCharm help at https://www.jetbrains.com/help/pycharm/
