-- 메세지 히스토리 조회를 위한 인덱스 설정
CREATE INDEX IF NOT EXISTS idx_message_room_id_id ON p_chat_message (chat_room_id, created_at DESC, chat_message_id DESC)