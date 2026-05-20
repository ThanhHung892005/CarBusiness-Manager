-- V17: Fix lead statuses inserted by V16 to match actual LeadStatus enum values
-- LeadStatus enum: NEW, CONTACTED, TEST_DRIVE, NEGOTIATING, CLOSED_WON, CLOSED_LOST
-- V16 mistakenly used QUALIFIED and PROPOSAL which don't exist in the enum

UPDATE leads SET status = 'TEST_DRIVE'   WHERE status = 'QUALIFIED';
UPDATE leads SET status = 'NEGOTIATING'  WHERE status = 'PROPOSAL';
