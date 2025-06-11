CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	username TEXT UNIQUE NOT NULL,
	email TEXT UNIQUE NOT NULL,
	password_hash TEXT NOT NULL,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
	last_login TIMESTAMPTZ,
	failed_login_attempts INT DEFAULT 0,
	lock_time TIMESTAMPTZ,
	is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE projects (
	id SERIAL PRIMARY KEY,
	name TEXT NOT NULL,
	description TEXT,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
	archived BOOLEAN DEFAULT FALSE,
	owner INT NOT NULL,
	FOREIGN KEY (owner)
		REFERENCES users(id)
		ON DELETE RESTRICT
);

CREATE TABLE users_projects (
	user_id INT NOT NULL,
	project_id INT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES users(id),
	FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE TYPE status as ENUM ('open', 'in_progress', 'done');

CREATE TABLE tasks (
	id SERIAL PRIMARY KEY,
	name TEXT NOT NULL,
	description TEXT,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
	due_date TIMESTAMPTZ,
	time_estimate INTERVAL,
	status status NOT NULL,
	project_id INT NOT NULL,
	assignee INT NOT NULL,
	FOREIGN KEY (project_id) REFERENCES projects(id)
		ON DELETE CASCADE,
	FOREIGN KEY (assignee) REFERENCES users(id)
		ON DELETE RESTRICT
);

CREATE TABLE activity (
	id SERIAL PRIMARY KEY,
	description TEXT NOT NULL,
	task_id INT NOT NULL,
	user_id INT NOT NULL,
	start_time TIMESTAMPTZ,
	end_time TIMESTAMPTZ,
	created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
	FOREIGN KEY (task_id) REFERENCES tasks(id)
		ON DELETE CASCADE,
	FOREIGN KEY (user_id) REFERENCES users(id)
		ON DELETE RESTRICT
);
