CREATE TABLE email_verify_tokens (
    token_id VARCHAR(30) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE TABLE users(
  user_id VARCHAR(30) PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL
);

CREATE TABLE user_stats (
    user_id VARCHAR(30) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    daily_goal INT NOT NULL DEFAULT 0,
    current_level INT NOT NULL DEFAULT 0,
    current_exp INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE practice_sessions (
    practice_id VARCHAR(30) PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    practice_type VARCHAR(30) NOT NULL,
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE notifications (
    notification_id VARCHAR(30) PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    notification_type VARCHAR(30) NOT NULL COMMENT 'GOAL_REMINDER, ACHIEVEMENT',
    title VARCHAR(100) NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE chat_messages (
    chat_id VARCHAR(30) PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    username VARCHAR(100) NOT NULL,
    current_level INT NOT NULL,
    avatar VARCHAR(2048) NOT NULL,
    message VARCHAR(100) NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE password_reset_tokens (
    token_id VARCHAR(30) PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE lessons (
    lesson_id VARCHAR(30) PRIMARY KEY,
    title VARCHAR(30) NOT NULL,
	description VARCHAR(100),
    is_available BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE questions (
    question_id VARCHAR(30) PRIMARY KEY,
    lesson_id VARCHAR(30) NOT NULL,
    -- The type of question, which tells your app how to interpret the JSON
    question_type VARCHAR(50) NOT NULL COMMENT 'SENTENCE_BUILDING, LISTENING' ,
    prompt_text TEXT COMMENT 'Core content or prompt for ALL question types',
    attributes JSON COMMENT 'Stores type-specific data as a JSON object',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id) ON DELETE CASCADE
);

CREATE TABLE answers (
    answer_id VARCHAR(30) PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    question_id VARCHAR(30) NOT NULL,
    lesson_id VARCHAR(30) NOT NULL,
    selected_answer TEXT, -- What the user actually chose/typed
    is_correct BOOLEAN NOT NULL,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
	FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id) ON DELETE CASCADE
);

CREATE TABLE flashcard_list (
  flashcard_list_id VARCHAR(30) PRIMARY KEY,
  user_id VARCHAR(30) NOT NULL,
  flashcard_number INT NOT NULL,
  title VARCHAR(25) NOT NULL,
  description VARCHAR(100),
  is_random BOOLEAN NOT NULL,
  default_language VARCHAR(30) NOT NULL,
  next_review_date DATE NOT NULL,
  current_streak INT NOT NULL DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE flashcard (
  flashcard_id VARCHAR(30) PRIMARY KEY,
  flashcard_list_id VARCHAR(30) NOT NULL,
  english_word VARCHAR(25) NOT NULL,
  malay_word VARCHAR(25) NOT NULL,
  FOREIGN KEY (flashcard_list_id) REFERENCES flashcard_list(flashcard_list_id) ON DELETE CASCADE
);

CREATE TABLE scenario(
  scenario_id VARCHAR(30) PRIMARY KEY,
  user_id VARCHAR(30) NOT NULL,
  title VARCHAR(25) NOT NULL,
  description VARCHAR(100),
  dialogue_number INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE dialogue (
  dialogue_id VARCHAR(30) PRIMARY KEY,
  scenario_id VARCHAR(30) NOT NULL,
  dialogue_type VARCHAR(10) NOT NULL,
  english VARCHAR(35) NOT NULL,
  malay VARCHAR(35) NOT NULL,
  dialogue_order VARCHAR(10) NOT NULL,
  audio_url VARCHAR(2048),
  FOREIGN KEY (scenario_id) REFERENCES scenario(scenario_id) ON DELETE CASCADE,
  CONSTRAINT uq_dialogue_order_in_scenario UNIQUE (scenario_id, dialogue_type, dialogue_order)
);

CREATE TABLE character_templates (
    template_id VARCHAR(30) PRIMARY KEY,
    character_name VARCHAR(255) NOT NULL,
    character_type VARCHAR(50) NOT NULL,
	image_url VARCHAR(255) NULL,
    unlock_level INT NOT NULL,
    base_hp INT NOT NULL,
    base_attack INT NOT NULL,
    base_defense INT NOT NULL,
    base_speed INT NOT NULL
) COMMENT='Defines the master list of all characters and their base stats.';


CREATE TABLE characters (
    character_id VARCHAR(30) PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    character_template_id VARCHAR(30) NOT NULL,
    unlocked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    character_status VARCHAR(30) NOT NULL COMMENT 'IDLE, LISTED_FOR_BATTLE',
    listed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_template (user_id, character_template_id) COMMENT 'A user cannot own the same character template twice',
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (character_template_id) REFERENCES character_templates(template_id) ON DELETE CASCADE
) COMMENT='Represents an instance of a character that a user has unlocked.';


CREATE TABLE battles (
    battle_id VARCHAR(30) PRIMARY KEY,
    challenger_id VARCHAR(30) NOT NULL,
    defender_id VARCHAR(30) NOT NULL,
    winner_id VARCHAR(30) NOT NULL,
    battle_log JSON NOT NULL COMMENT 'Stores the entire turn-by-turn replay as a JSON array',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (challenger_id) REFERENCES characters(character_id) ON DELETE CASCADE,
	FOREIGN KEY (defender_id) REFERENCES characters(character_id) ON DELETE CASCADE,
	FOREIGN KEY (winner_id) REFERENCES characters(character_id) ON DELETE CASCADE
) COMMENT='Stores the outcome and replay log of each battle.';