import os

# Set the path to your FE5 folder
fe5_folder = r'D:\Karan Ticket Project\TicketServiceManagementV100\Text FIles\FE5'

# Output file path
output_file = os.path.join(fe5_folder, 'all_code.txt')

# Optional: file extensions to include
include_extensions = {'.html', '.htm', '.js', '.css', '.json', '.txt'}

with open(output_file, 'w', encoding='utf-8') as outfile:
    for root, dirs, files in os.walk(fe5_folder):
        for filename in files:
            file_path = os.path.join(root, filename)
            file_ext = os.path.splitext(filename)[1].lower()
            if file_ext in include_extensions:
                try:
                    with open(file_path, 'r', encoding='utf-8') as infile:
                        rel_path = os.path.relpath(file_path, fe5_folder)
                        outfile.write(f"\n\n===== FILE: {rel_path} =====\n\n")
                        outfile.write(infile.read())
                except Exception as e:
                    print(f"Error reading {file_path}: {e}")