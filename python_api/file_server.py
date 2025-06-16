from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.responses import FileResponse
from pathlib import Path
import os

app = FastAPI()

# 파일을 저장할 기본 디렉토리 설정
UPLOAD_DIRECTORY = Path("./uploaded_files")
UPLOAD_DIRECTORY.mkdir(parents=True, exist_ok=True)

@app.post("/uploadfile/")
async def create_upload_file(file: UploadFile = File(...)):
    """
    단일 파일을 업로드하는 엔드포인트입니다.
    업로드된 파일은 서버의 `uploaded_files` 디렉토리에 저장됩니다.
    """
    file_location = UPLOAD_DIRECTORY / file.filename
    try:
        with open(file_location, "wb+") as file_object:
            file_object.write(await file.read())
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"파일 저장 중 오류 발생: {e}")
    return {"filename": file.filename, "message": f"파일 '{file.filename}'이(가) 성공적으로 업로드되었습니다.", "file_path": str(file_location)}

@app.get("/downloadfile/{filename}")
async def download_file(filename: str):
    """
    지정된 파일을 다운로드하는 엔드포인트입니다.
    파일은 `uploaded_files` 디렉토리에서 찾습니다.
    """
    file_path = UPLOAD_DIRECTORY / filename
    if not file_path.is_file():
        raise HTTPException(status_code=404, detail="파일을 찾을 수 없습니다.")
    
    return FileResponse(path=file_path, filename=filename, media_type='application/octet-stream')

@app.get("/")
async def read_root():
    return {"message": "FastAPI 파일 서버가 실행 중입니다. /uploadfile/ 이나 /downloadfile/{filename}을 사용하세요."} 