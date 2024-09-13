from flask import Flask, request, jsonify, flash, render_template, send_from_directory
from werkzeug.utils import secure_filename
import os
import mimetypes
import json

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = 'uploads'  # Base directory for uploads
app.config['MAX_CONTENT_LENGTH'] = 100 * 1024 * 1024  # Max upload size, e.g., 100MB
app.config['APPLICATION_ROOT'] = '/myflaskapp'

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif', 'mp4', 'avi', 'mov', 'pdf', 'txt', 'doc', 'docx'}

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/files', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return jsonify({"status": "error", "message": "No file part"}), 400
    file = request.files['file']
    if file.filename == '':
        return jsonify({"status": "error", "message": "No selected file"}), 400
    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)
        user_email = request.form['email']  # Assuming email is sent as form data
        if user_email == '':
            user_email = 'others'
        user_dir = os.path.join(app.config['UPLOAD_FOLDER'], user_email)

        # Create user directory if it doesn't exist
        if not os.path.exists(user_dir):
            os.makedirs(user_dir)

        # Determine file type and set the appropriate subdirectory
        file_type = mimetypes.guess_type(filename)[0]
        if file_type and file_type.startswith('image'):
            subdir = 'images'
        elif file_type and file_type.startswith('video'):
            subdir = 'videos'
        else:
            subdir = 'files'

        # Create subdirectory if it doesn't exist
        subdir_path = os.path.join(user_dir, subdir)
        if not os.path.exists(subdir_path):
            os.makedirs(subdir_path)

        # Save file
        file.save(os.path.join(subdir_path, filename))
        return jsonify({"status": "success", "message": "File uploaded successfully"}), 200

    return jsonify({"status": "error", "message": "File type not allowed"}), 400

@app.route('/location', methods=['POST'])
def location():
    if request.is_json:
        data = request.get_json()
        print("Received location:", data)
        try:
            # Check for duplicates first
            existing_data = []
            with open('location.txt', 'r') as f:
                existing_data = f.read().splitlines()

            # Convert the dict to a JSON string and check if it's in the file
            data_str = json.dumps(data) + '\n'
            if data_str.strip() not in existing_data:
                # Append the new data if it's not a duplicate
                with open('location.txt', 'a') as f:
                    f.write(data_str)
            return jsonify({"status": "success", "message": "Location received and processed"}), 200

        except FileNotFoundError:
            # If the file does not exist, create it and write the data
            with open('location.txt', 'w') as f:
                f.write(data_str)
            return jsonify({"status": "success", "message": "Location received and new file created"}), 200

        except Exception as e:
            app.logger.error("Failed to save location: %s", e)
            return jsonify({"status": "error", "message": "Failed to process the location"}), 500
    else:
        return jsonify({"status": "error", "message": "Request body must be JSON"}), 400

@app.route('/contacts', methods=['POST'])
def contacts():
    if request.is_json:
        data = request.get_json()
        print("Received contacts:", data)

        existing_contacts = set()
        # Load existing contacts
        try:
            with open('contacts.txt', 'r') as f:
                for line in f:
                    name, phone = line.strip().split(', Phone: ')
                    existing_contacts.add((name, phone))
        except FileNotFoundError:
            pass  # It's okay if the file doesn't exist yet

        # Save new unique contacts to file
        with open('contacts.txt', 'a') as f:
            for contact in data:
                name_phone_pair = (f"Name: {contact['name']}", contact['phone'])
                if name_phone_pair not in existing_contacts:
                    f.write(f"{name_phone_pair[0]}, Phone: {name_phone_pair[1]}\n")
                    existing_contacts.add(name_phone_pair)  # Add to set to check for future duplicates

        return jsonify({"status": "success", "message": "Contacts received and processed"}), 200
    else:
        return jsonify({"status": "error", "message": "Request body must be JSON"}), 400

@app.route('/admin', methods=['GET'])
def admin():
    # Read and return the contents of the location and contacts files
    try:
        with open('location.txt', 'r') as location_file:
            locations = location_file.readlines()
    except FileNotFoundError:
        locations = ['No location data available.']

    try:
        with open('contacts.txt', 'r') as contacts_file:
            contacts = contacts_file.readlines()
    except FileNotFoundError:
        contacts = ['No contacts data available.']

    # Get the list of user directories in the uploads folder
    user_dirs = os.listdir(app.config['UPLOAD_FOLDER'])

    # Create a dictionary to store the files for each user
    user_files = {}
    for user_dir in user_dirs:
        user_files[user_dir] = {'images': [], 'videos': []}
        user_path = os.path.join(app.config['UPLOAD_FOLDER'], user_dir)
        for category in ['images', 'videos']:
            category_path = os.path.join(user_path, category)
            if os.path.exists(category_path):
                user_files[user_dir][category] = os.listdir(category_path)

    return render_template('admin.html', locations=locations, contacts=contacts, user_files=user_files)

@app.route('/uploads/<path:filename>')
def uploaded_file(filename):
    full_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
    app.logger.info(f"Full path: {full_path}")
    if os.path.isfile(full_path):
        return send_from_directory(app.config['UPLOAD_FOLDER'], filename)
    else:
        app.logger.error(f"File not found: {full_path}")
        return "File not found", 404

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5005)
