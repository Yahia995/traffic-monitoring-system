# Models
VEHICLE_MODEL_PATH = "models/vehicle_yolo.pt"
PLATE_MODEL_PATH =  "models/plate_yolo.pt"

# Speed / Physics calibration
PIXEL_TO_METER = 0.05   # tune later with readl strret calibration
SPEED_LIMIT = 50.0      # km/h

# Tracking  settings
FRAME_SKIP = 1          # skip n frame each time -> faster
MIN_TRACKED_FRAMES = 8    # minimum frames to calculate speed

# Centroid Tracker settings
MAX_DISAPPEARED = 60     # maximum consecutive frames object can be lost
MAX_DISTANCE = 70       # maximum distance for association

# File upload settings
MAX_UPLOAD_MB = 200
ALLOWED_EXT = (".mp4", ".avi", ".mov", ".mkv")