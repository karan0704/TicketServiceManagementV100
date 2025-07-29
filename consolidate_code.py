import os
from datetime import datetime
from pathlib import Path

def consolidate_frontend_files(static_folder_path, output_filename="consolidated_frontend.txt"):
    """
    Consolidate only HTML, CSS, and JS files from the static folder

    Args:
        static_folder_path: Path to the static folder
        output_filename: Name of the output consolidated file
    """

    # Frontend file extensions only
    frontend_extensions = ['.html', '.css', '.js']

    # Check if static folder exists
    if not os.path.exists(static_folder_path):
        print(f"❌ Static folder not found: {static_folder_path}")
        return False

    # Output file path (in the same static folder)
    output_file = os.path.join(static_folder_path, output_filename)

    print(f"🔍 Processing frontend files in: {static_folder_path}")
    print(f"📄 Output file: {output_file}")

    consolidated_content = []
    file_count = 0

    # Add header
    consolidated_content.extend([
        "=" * 80,
        "CONSOLIDATED FRONTEND CODE (HTML, CSS, JS)",
        f"Project: TicketServiceManagementV100",
        f"Source: {static_folder_path}",
        f"Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
        "=" * 80,
        ""
    ])

    # Walk through static folder and subfolders
    for root, dirs, files in os.walk(static_folder_path):
        for file in files:
            file_path = os.path.join(root, file)
            file_extension = os.path.splitext(file)[1].lower()

            # Skip the output file itself
            if file == output_filename:
                continue

            # Check if it's a frontend file
            if file_extension in frontend_extensions:
                try:
                    # Get relative path within static folder
                    relative_path = os.path.relpath(file_path, static_folder_path)

                    # Read file content
                    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                        content = f.read()

                    # Add file info and content
                    consolidated_content.extend([
                        "-" * 60,
                        f"FILE: {relative_path}",
                        f"TYPE: {file_extension.upper()[1:]} File",
                        f"PATH: {file_path}",
                        f"SIZE: {len(content)} characters",
                        f"LINES: {len(content.splitlines())}",
                        "-" * 60,
                        content,
                        "",
                        ""
                    ])

                    file_count += 1
                    print(f"  ✅ Added: {relative_path}")

                except Exception as e:
                    print(f"  ❌ Error reading {relative_path}: {str(e)}")
                    consolidated_content.extend([
                        f"ERROR READING FILE: {relative_path}",
                        f"Error: {str(e)}",
                        ""
                    ])

    # Add summary
    consolidated_content.extend([
        "=" * 80,
        "CONSOLIDATION SUMMARY",
        "=" * 80,
        f"Total frontend files processed: {file_count}",
        f"File types included: HTML, CSS, JavaScript",
        f"Source folder: {static_folder_path}",
        f"Generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}",
        "=" * 80
    ])

    # Write consolidated file
    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write('\n'.join(consolidated_content))

        file_size_kb = os.path.getsize(output_file) / 1024
        print(f"✅ Successfully consolidated {file_count} frontend files")
        print(f"📄 Output saved to: {output_file}")
        print(f"📏 File size: {file_size_kb:.2f} KB")
        return True

    except Exception as e:
        print(f"❌ Error writing output file: {str(e)}")
        return False

def get_frontend_file_stats(static_folder_path):
    """
    Get statistics about frontend files in the static folder
    """
    frontend_extensions = ['.html', '.css', '.js']
    stats = {'html': 0, 'css': 0, 'js': 0, 'total': 0}

    if not os.path.exists(static_folder_path):
        return stats

    for root, dirs, files in os.walk(static_folder_path):
        for file in files:
            file_extension = os.path.splitext(file)[1].lower()
            if file_extension in frontend_extensions:
                stats['total'] += 1
                if file_extension == '.html':
                    stats['html'] += 1
                elif file_extension == '.css':
                    stats['css'] += 1
                elif file_extension == '.js':
                    stats['js'] += 1

    return stats

# Main execution
if __name__ == "__main__":
    # Your static folder path
    STATIC_FOLDER = r"D:\Karan Ticket Project\TicketServiceManagementV100\src\main\resources\static"

    print("🚀 Starting frontend file consolidation...")
    print("🎯 Target: HTML, CSS, and JavaScript files only")

    # Get file statistics first
    stats = get_frontend_file_stats(STATIC_FOLDER)
    print(f"\n📊 Frontend files found:")
    print(f"  📝 HTML files: {stats['html']}")
    print(f"  🎨 CSS files: {stats['css']}")
    print(f"  ⚡ JavaScript files: {stats['js']}")
    print(f"  📁 Total frontend files: {stats['total']}")

    if stats['total'] == 0:
        print("❌ No frontend files found in the static folder!")
    else:
        print(f"\n{'='*50}")
        # Consolidate the frontend files
        success = consolidate_frontend_files(STATIC_FOLDER)

        if success:
            print("\n🎉 Frontend consolidation completed successfully!")
        else:
            print("\n❌ Frontend consolidation failed!")