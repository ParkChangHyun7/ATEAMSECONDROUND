import requests
import json

def chat_with_ollama(model_name, prompt):
    """
    Ollama 서버와 통신하여 챗봇 응답을 받습니다.

    Args:
        model_name (str): 사용할 Ollama 모델의 이름 (예: "gemma:latest" 또는 "my-deepseek-qwen").
        prompt (str): 챗봇에게 보낼 질문.

    Returns:
        str: 챗봇의 응답 텍스트.
    """
    url = "http://localhost:11434/api/generate"
    headers = {"Content-Type": "application/json"}
    data = {
        "model": model_name,
        "prompt": prompt,
        "stream": False # 스트리밍 없이 한 번에 응답 받기
    }

    try:
        response = requests.post(url, headers=headers, data=json.dumps(data))
        response.raise_for_status() # HTTP 에러 발생 시 예외 발생

        result = response.json()
        return result.get("response", "응답을 받지 못했습니다.")

    except requests.exceptions.ConnectionError:
        return "Ollama 서버에 연결할 수 없습니다. Ollama 서버가 실행 중인지 확인하세요."
    except requests.exceptions.RequestException as e:
        return f"요청 중 오류 발생: {e}"

if __name__ == "__main__":
    # 대장님께서 Ollama에 임포트한 모델 이름으로 변경해주세요!
    # 예: model_to_use = "my-deepseek-qwen"
    model_to_use = "gemma:latest" # 기본값 (Ollama에 gemma 모델이 있다면 사용 가능)
    
    user_input = input("Ollama 챗봇에게 질문하세요 (종료하려면 'q' 입력): ")

    while user_input.lower() != 'q':
        print(f"모델 ({model_to_use})에게 질문 중...")
        response_text = chat_with_ollama(model_to_use, user_input)
        print("Ollama 챗봇:", response_text)
        user_input = input("Ollama 챗봇에게 질문하세요 (종료하려면 'q' 입력): ")

    print("챗봇과의 대화를 종료합니다.")
