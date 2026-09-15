INSERT INTO user_table (
    user_id,
    user_email,
    password_hash,
    name,
    surname,
    user_name,
    role,
    has_finished_onboarding,
    has_verified_email
)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           'system@internal.local',
           'not_a_real_hash_disabled_login',
           'System',
           'Admin',
           'system_admin',
           'SYSTEM',
           true,
           true
       );

-- Default (fallback) categories, one per category_type
INSERT INTO categories_table (user_id, category_type, name, description, is_default)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'INCOME',           'Default Income Category',           'Category that user defaults when deleting category that is still being used', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE',          'Default Expense Category',          'Category that user defaults when deleting category that is still being used', true),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER_FROM',    'Default Transfer From Category',    'Category that user defaults when deleting category that is still being used', true),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER_TO',      'Default Transfer To Category',      'Category that user defaults when deleting category that is still being used', true),
    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT_PLUS',  'Default Adjustment Plus Category',  'Category that user defaults when deleting category that is still being used', true),
    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT_MINUS', 'Default Adjustment Minus Category', 'Category that user defaults when deleting category that is still being used', true);

-- Everyday categories
INSERT INTO categories_table (user_id, category_type, name, description, is_default)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Salary Income',       'Regular income from employment or wages', true),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Freelance Income',    'Income from freelance or contract work', true),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Investment Income',   'Dividends, interest, and capital gains', true),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Gift Received',       'Money received as a gift', true),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Refund',              'Refunds and reimbursements received', true),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Rental Income',       'Income received from renting out property', true),
    ('00000000-0000-0000-0000-000000000001', 'INCOME', 'Business Income',    'Income generated from owning or running a business', true),

    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Retail Store Purchases', 'General purchases from retail stores', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Groceries',              'Food and household supplies', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Rent / Mortgage',        'Housing payments', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Utilities',              'Electricity, water, gas, internet, etc.', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Transportation',         'Fuel, public transit, ride-shares, parking', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Dining Out',             'Restaurants, cafes, and takeout', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Entertainment',          'Movies, streaming, hobbies, events', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Healthcare',             'Medical expenses, pharmacy, insurance copays', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Subscriptions',          'Recurring software and service subscriptions', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Insurance',              'Insurance premiums (non-health)', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Education',              'Tuition, courses, books, supplies', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Travel',                 'Flights, hotels, vacations', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Personal Care',          'Haircuts, cosmetics, gym memberships, and similar', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Pet Care',               'Pet food, vet visits, grooming, and supplies', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Charity / Donations',    'Charitable giving and donations', true),
    ('00000000-0000-0000-0000-000000000001', 'EXPENSE', 'Taxes',                  'Income, property, or other tax payments', true),

    ('00000000-0000-0000-0000-000000000001', 'TRANSFER_FROM', 'Incoming Account Transfer', 'Funds moved in from another one of the user''s own accounts', true),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER_FROM', 'Savings Withdrawal',        'Transfer in from a savings account', true),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER_FROM', 'Loan Disbursement',         'Transfer in from a loan being received', true),

    ('00000000-0000-0000-0000-000000000001', 'TRANSFER_TO', 'Outgoing Account Transfer', 'Funds moved out to another one of the user''s own accounts', true),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER_TO', 'Savings Deposit',           'Transfer out into a savings account', true),
    ('00000000-0000-0000-0000-000000000001', 'TRANSFER_TO', 'Loan Repayment',            'Transfer out made to repay a loan', true),

    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT_PLUS',  'Balance Correction (Increase)',      'Manual upward correction of account balance', true),
    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT_PLUS',  'Currency Reconciliation (Gain)',     'Positive adjustment due to exchange rate or rounding', true),

    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT_MINUS', 'Balance Correction (Decrease)',      'Manual downward correction of account balance', true),
    ('00000000-0000-0000-0000-000000000001', 'ADJUSTMENT_MINUS', 'Currency Reconciliation (Loss)',     'Negative adjustment due to exchange rate or rounding', true);