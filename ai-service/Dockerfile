FROM python:3.10-slim

# Environment
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Force CPU-only
ENV CUDA_VISIBLE_DEVICES=""
ENV NVIDIA_VISIBLE_DEVICES=none
ENV TORCH_DEVICE=cpu

ENV TORCH_HOME=/tmp/torch
ENV ULTRALYTICS_CACHE_DIR=/tmp/ultralytics

# System dependencies
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libgl1 \
    libglib2.0-0 \
    libgomp1 \
    libsm6 \
    libxext6 \
    libxrender1 \
    gcc \
    g++ \
 && rm -rf /var/lib/apt/lists/*

# App setup
WORKDIR /app
COPY requirements.txt .

# Python dependencies
RUN pip install --upgrade pip \
 && pip install --no-cache-dir \
    torch==2.5.1+cpu \
    torchvision==0.20.1+cpu \
    --index-url https://download.pytorch.org/whl/cpu \
 && pip install --no-cache-dir -r requirements.txt \
 && apt-get purge -y gcc g++ \
 && apt-get autoremove -y \
 && rm -rf /var/lib/apt/lists/* /root/.cache /tmp/*

# Copy source
COPY . .

# Runtime
EXPOSE 8000
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]