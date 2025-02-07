import streamlit as st
from pyzbar.pyzbar import decode
from PIL import Image, ImageDraw

st.title("Loan Inventory Barcode Scanner")

# 1️⃣ Ensure session state variables exist
if "uploader_key" not in st.session_state:
    st.session_state.uploader_key = "uploader_loan_inventory"  # Unique key for file uploader
if "uploaded_file" not in st.session_state:
    st.session_state.uploaded_file = None  # Stores the uploaded file
if "checkbox_state" not in st.session_state:
    st.session_state.checkbox_state = False  # Default checkbox state


# 2️⃣ Function to reset file uploader & UI
def clear_upload():
    st.session_state.uploaded_file = None  # Remove uploaded file
    st.session_state.uploader_key = f"uploader_{st.session_state.uploader_key}_reset"  # Change uploader key
    st.session_state.checkbox_state = False  # Reset checkbox
    st.rerun()  # ✅ Force Streamlit to immediately refresh UI


# 3️⃣ File uploader with dynamic key (to force reset)
uploaded_file = st.file_uploader(
    "Upload an image with a barcode",
    type=["jpg", "png", "jpeg"],
    accept_multiple_files=False,
    key=st.session_state.uploader_key  # Use dynamic key to force reset
)

# 5️⃣ Store the uploaded file in session state if a new one is selected
if uploaded_file:
    st.session_state.uploaded_file = uploaded_file

# 6️⃣ Process and display image if uploaded
if st.session_state.uploaded_file:
    image = Image.open(st.session_state.uploaded_file)
    draw = ImageDraw.Draw(image)
    barcodes = decode(image)

    if barcodes:
        for barcode in barcodes:
            barcode_data = barcode.data.decode("utf-8")
            rect = barcode.rect
            draw.rectangle(
                [(rect.left, rect.top), (rect.left + rect.width, rect.top + rect.height)],
                outline="red", width=5
            )
            st.write(f"Coordinates: Top-Left ({rect.left}, {rect.top}), Width: {rect.width}, Height: {rect.height}")
            st.image(image, caption="Detected Barcodes", use_container_width=True)
            st.success(f"Detected Barcode: {barcode_data}")
            # 4️⃣ Clear button that resets everything
            if st.button("Clear"):
                clear_upload()  # ✅ Calls function that resets and reruns app

    else:
        st.warning("No barcodes detected.")
