//const CLIENT_ID = '845430548717-e27otsbdprsb18vhe2slj8cfsio19mg5.apps.googleusercontent.com'; // <-- PASTE YOUR CLIENT ID HERE
const { useState, useEffect, useCallback, useRef } = React;
const { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, BarChart, Bar } = Recharts;

// --- Google API Configuration ---
// IMPORTANT: You need to replace these with your own credentials for the "Add Entry" tab to work.
const CLIENT_ID = '845430548717-e27otsbdprsb18vhe2slj8cfsio19mg5.apps.googleusercontent.com'; 
const SPREADSHEET_ID = '1tw86AUy7oP4KMWTi-phkF1Z6Dw3qkoioVdMedqmP2no';

// --- Helper Icons (as SVG components for the web) ---
const BottleIcon = ({ className }) => (
    <svg className={className} height="24" width="24" viewBox="0 0 24 24" fill="currentColor">
      <path d="M14.79,3.32C14.58,3.11 14.3,3 14,3H10C9.7,3 9.42,3.11 9.21,3.32L5.87,6.66L5.53,6.32C5.32,6.11 5.03,6 4.75,6C4.47,6 4.18,6.11 3.97,6.32L3.32,6.97C3.11,7.18 3,7.47 3,7.75C3,8.03 3.11,8.32 3.32,8.53L4.69,9.9L3,11.59V20C3,20.55 3.45,21 4,21H12V19H5V12.59L6.41,11.18L8.53,13.31C8.74,13.52 9,13.63 9.27,13.63C9.55,13.63 9.82,13.52 10.03,13.31L12,11.34L13.97,13.31C14.18,13.52 14.45,13.63 14.73,13.63C15,13.63 15.27,13.52 15.48,13.31L17.59,11.18L19,12.59V19H17V21H20C20.55,21 21,20.55 21,20V11.59L19.31,9.9L20.68,8.53C20.89,8.32 21,8.03 21,7.75C21,7.47 20.89,7.18 20.68,6.97L20.03,6.32C19.82,6.11 19.53,6 19.25,6C18.97,6 18.68,6.11 18.47,6.32L18.13,6.66L14.79,3.32M10,5H14L16.71,7.71L16,8.41L14,6.41L12,8.41L10,6.41L8,8.41L7.29,7.71L10,5Z" />
    </svg>
  );
  const CalendarIcon = ({ className }) => (
    <svg className={className} height="24" width="24" viewBox="0 0 24 24" fill="currentColor">
      <path d="M19,4H18V2H16V4H8V2H6V4H5C3.89,4 3,4.9 3,6V20A2,2 0 0,0 5,22H19A2,2 0 0,0 21,20V6C21,4.9 20.1,4 19,4M19,20H5V10H19V20M19,8H5V6H19V8M12,13H17V18H12V13Z" />
    </svg>
  );
  const ChartIcon = ({ className }) => (
      <svg className={className} height="24" width="24" viewBox="0 0 24 24" fill="currentColor">
          <path d="M22 21H2V3h2v16h18v2zM10 17l-4-4-1.41 1.41L10 19.84l7.41-7.42L16 11l-6 6.01z" />
      </svg>
  );
  const ClockIcon = ({ className }) => (
      <svg className={className} height="24" width="24" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z" />
      </svg>
  );
  const SunIcon = ({ className }) => (
      <svg className={className} height="24" width="24" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12,9A3,3 0 0,0 9,12A3,3 0 0,0 12,15A3,3 0 0,0 15,12A3,3 0 0,0 12,9M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M12,4A8,8 0 0,1 20,12A8,8 0 0,1 12,20A8,8 0 0,1 4,12A8,8 0 0,1 12,4Z" />
      </svg>
  );
  const TrashIcon = ({ className }) => (
      <svg className={className} height="24" width="24" viewBox="0 0 24 24" fill="currentColor">
          <path d="M9,3V4H4V6H5V19A2,2 0 0,0 7,21H17A2,2 0 0,0 19,19V6H20V4H15V3H9M7,6H17V19H7V6M9,8V17H11V8H9M13,8V17H15V8H13Z" />
      </svg>
  );
  const GoogleIcon = () => (<svg viewBox="0 0 48 48"><path fill="#FFC107" d="M43.611,20.083H42V20H24v8h11.303c-1.649,4.657-6.08,8-11.303,8c-6.627,0-12-5.373-12-12c0-6.627,5.373-12,12-12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C12.955,4,4,12.955,4,24c0,11.045,8.955,20,20,20c11.045,0,20-8.955,20-20C44,22.659,43.862,21.35,43.611,20.083z"></path><path fill="#FF3D00" d="M6.306,14.691l6.571,4.819C14.655,15.108,18.961,12,24,12c3.059,0,5.842,1.154,7.961,3.039l5.657-5.657C34.046,6.053,29.268,4,24,4C16.318,4,9.656,8.337,6.306,14.691z"></path><path fill="#4CAF50" d="M24,44c5.166,0,9.86-1.977,13.409-5.192l-6.19-5.238C29.211,35.091,26.715,36,24,36c-5.202,0-9.619-3.317-11.283-7.946l-6.522,5.025C9.505,39.556,16.227,44,24,44z"></path><path fill="#1976D2" d="M43.611,20.083H42V20H24v8h11.303c-0.792,2.237-2.231,4.166-4.087,5.574l6.19,5.238C42.022,36.217,44,30.563,44,24C44,22.659,43.862,21.35,43.611,20.083z"></path></svg>);
  
  // --- Main App Component ---
  function App() {
    const [activeTab, setActiveTab] = useState('dashboard');
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [gapiInited, setGapiInited] = useState(false);
    const [gisInited, setGisInited] = useState(false);
    const [tokenClient, setTokenClient] = useState(null);
    const [isSignedIn, setIsSignedIn] = useState(false);
  
    const fetchData = useCallback(async (showLoading = true) => {
      if (showLoading) setLoading(true);
      setError(null);
      const url = `https://docs.google.com/spreadsheets/d/1tw86AUy7oP4KMWTi-phkF1Z6Dw3qkoioVdMedqmP2no/gviz/tq?tqx=out:json&gid=0&tq=select%20A,B,C,D,E,F,G`;
  
      try {
        const response = await fetch(url);
        const text = await response.text();
        const match = text.match(/google\.visualization\.Query\.setResponse\((.*)\);/s);
        if (!match || !match[1]) {
          throw new Error("Invalid response from Sheets. Check sheet's sharing permissions are set to 'Anyone with the link'.");
        }
        const jsonString = match[1];
        const parsedData = JSON.parse(jsonString);
  
        if (parsedData.status !== 'ok') throw new Error('Failed to fetch data from Google Sheets.');
  
        const formattedData = parsedData.table.rows.map(row => {
            // Robust parsing: Ensure the essential cells and their values exist before processing.
            if (!row || !row.c || !row.c[0] || !row.c[0].v || !row.c[1] || !row.c[1].v || row.c[5] === null) {
                return null;
            }
            const dateMatch = row.c[0].v.match(/Date\((\d+),(\d+),(\d+)\)/);
            const timeMatch = row.c[1].v.match(/Date\((.*?)\)/); // More robust regex for time
            if (!dateMatch || !timeMatch) return null;
            
            // The time part is tricky. We'll extract only HH, MM, SS from it.
            const timeArgs = timeMatch[1].split(',').map(s => s.trim());
            const hours = timeArgs.length > 3 ? parseInt(timeArgs[3], 10) : 0;
            const minutes = timeArgs.length > 4 ? parseInt(timeArgs[4], 10) : 0;
  
            const [ , year, month, day] = dateMatch.map(Number);
            const date = new Date(year, month, day, hours, minutes);
            
            return {
              date, milkVolume: row.c[2]?.v || 0, finished: row.c[3]?.v || false,
              notes: row.c[4]?.v || '', consumed: row.c[5]?.v || 0, notConsumed: row.c[6]?.v || 0,
            };
        }).filter(Boolean).sort((a, b) => b.date - a.date);
        setData(formattedData);
      } catch (e) {
        console.error(e);
        setError(e.message || 'Could not load feeding data. Please check your connection or the sheet URL.');
      } finally {
        if (showLoading) setLoading(false);
      }
    }, []);
  
    useEffect(() => {
        // Load gapi
        const scriptGapi = document.createElement('script');
        scriptGapi.src = 'https://apis.google.com/js/api.js';
        scriptGapi.async = true;
        scriptGapi.defer = true;
        scriptGapi.onload = () => window.gapi.load('client', () => {
          window.gapi.client
            .init({
              // No clientId here; discoveryDocs is enough
              discoveryDocs: ['https://sheets.googleapis.com/$discovery/rest?version=v4'],
            })
            .then(() => setGapiInited(true));
        });
        document.body.appendChild(scriptGapi);
      
        // Load GIS
        const scriptGis = document.createElement('script');
        scriptGis.src = 'https://accounts.google.com/gsi/client';
        scriptGis.async = true;
        scriptGis.defer = true;
        scriptGis.onload = () => {
          const client = window.google.accounts.oauth2.initTokenClient({
            client_id: CLIENT_ID,
            scope: 'https://www.googleapis.com/auth/spreadsheets',
            callback: (tokenResponse) => {
              if (tokenResponse?.access_token) {
                // IMPORTANT: give the token to gapi so Sheets calls are authorized
                window.gapi.client.setToken({ access_token: tokenResponse.access_token });
                setIsSignedIn(true);
              }
            },
          });
          setTokenClient(client);
          setGisInited(true);
        };
        document.body.appendChild(scriptGis);
      }, []);
  
    useEffect(() => {
      fetchData();
    }, [fetchData]);
  
    const handleSignIn = () => {
      if (CLIENT_ID === 'YOUR_CLIENT_ID.apps.googleusercontent.com') {
          alert("Please replace the placeholder CLIENT_ID in App.jsx with your actual Google Client ID.");
          return;
      }
      if (tokenClient) {
        tokenClient.requestAccessToken();
      }
    };
    
    const handleSignOut = () => {
      const token = window.gapi.client.getToken();
      if (token) {
        window.google.accounts.oauth2.revoke(token.access_token, () => {
          window.gapi.client.setToken('');
          setIsSignedIn(false);
        });
      }
    };
  
    const onDataAdded = () => {
      fetchData(false);
      setActiveTab('dashboard');
    };
    
    return (
      <main className="bg-gray-50 min-h-screen p-4 sm:p-6 md:p-8 font-sans">
        <div className="max-w-7xl mx-auto">
          <header className="mb-6">
            <h1 className="text-4xl font-bold text-gray-800">Baby's Milk Tracker</h1>
            <p className="text-lg text-gray-500">Daily Consumption Dashboard</p>
          </header>
          <div className="border-b border-gray-200 mb-8">
              <nav className="-mb-px flex space-x-6" aria-label="Tabs">
                  <button
                      onClick={() => setActiveTab('dashboard')}
                      className={`${activeTab === 'dashboard' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'} whitespace-nowrap py-4 px-1 border-b-2 font-medium text-lg transition-colors`}
                  >
                      Dashboard
                  </button>
                  <button
                      onClick={() => setActiveTab('addEntry')}
                      className={`${activeTab === 'addEntry' ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'} whitespace-nowrap py-4 px-1 border-b-2 font-medium text-lg transition-colors`}
                  >
                      Add Entry
                  </button>
              </nav>
          </div>
          {activeTab === 'dashboard' && <DashboardComponent data={data} loading={loading} error={error} fetchData={fetchData} />}
          {activeTab === 'addEntry' && <AddEntryComponent isSignedIn={isSignedIn} handleSignIn={handleSignIn} handleSignOut={handleSignOut} onDataAdded={onDataAdded} apiReady={gapiInited && gisInited} />}
        </div>
      </main>
    );
  }
  
  function DashboardComponent({ data, loading, error, fetchData }) {
      if (loading) {
          return (
            <div className="flex items-center justify-center h-96 bg-gray-50">
              <div className="text-center">
                   <svg className="mx-auto h-12 w-12 text-blue-500 animate-spin" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <p className="mt-4 text-lg text-gray-600">Loading Feeding Data...</p>
              </div>
            </div>
          );
      }
      if (error) {
          return (
            <div className="flex items-center justify-center h-96 bg-red-50">
              <div className="text-center p-8 bg-white rounded-lg shadow-md">
                  <p className="text-lg text-red-600 mb-4">{error}</p>
                  <button onClick={() => fetchData(true)} className="px-6 py-2 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition-colors">
                    Retry
                  </button>
              </div>
            </div>
          );
      }
      const getTodayStart = () => new Date(new Date().setHours(0, 0, 0, 0));
      const todayStart = getTodayStart();
      const yesterdayStart = new Date(todayStart.getTime() - 24 * 60 * 60 * 1000);
      const todayConsumption = data.filter(item => item.date >= todayStart).reduce((sum, item) => sum + item.consumed, 0);
      const yesterdayConsumption = data.filter(item => item.date >= yesterdayStart && item.date < todayStart).reduce((sum, item) => sum + item.consumed, 0);
      const dailyData = data.reduce((acc, item) => {
          const day = item.date.toLocaleDateString('en-CA');
          if (!acc[day]) acc[day] = { consumedTotal: 0, notConsumedTotal: 0, count: 0 };
          acc[day].consumedTotal += item.consumed;
          acc[day].notConsumedTotal += item.notConsumed;
          acc[day].count += 1;
          return acc;
      }, {});
      const dailyTotals = Object.values(dailyData).map(d => d.consumedTotal);
      const averageConsumption = dailyTotals.length > 0 ? (dailyTotals.reduce((a, b) => a + b, 0) / dailyTotals.length).toFixed(0) : 0;
      const last24hConsumption = data.filter(item => (new Date() - item.date) < 24 * 60 * 60 * 1000).reduce((sum, item) => sum + item.consumed, 0);
      const dailyCounts = Object.values(dailyData).map(d => d.count);
      const avgFeedsPerDay = dailyCounts.length > 0 ? (dailyCounts.reduce((a, b) => a + b, 0) / dailyCounts.length).toFixed(1) : 0;
      const totalWastedLast7Days = Object.keys(dailyData).sort().slice(-7).reduce((sum, day) => sum + dailyData[day].notConsumedTotal, 0);
      const lineChartData = Array.from({ length: 7 }, (_, i) => {
          const d = new Date(todayStart);
          d.setDate(d.getDate() - (6 - i));
          const dayKey = d.toLocaleDateString('en-CA');
          return { name: d.toLocaleDateString('en-US', { weekday: 'short' }), Consumption: dailyData[dayKey]?.consumedTotal || 0 };
      });
      const dailyBreakdownChartData = Array.from({ length: 7 }, (_, i) => {
          const d = new Date(todayStart);
          d.setDate(d.getDate() - (6 - i));
          const dayKey = d.toLocaleDateString('en-CA');
          return { name: d.toLocaleDateString('en-US', { day: 'numeric', month: 'short' }), Consumed: dailyData[dayKey]?.consumedTotal || 0, Wasted: dailyData[dayKey]?.notConsumedTotal || 0 };
      });
      const hourlyAvgChartData = () => {
          const hourlyData = Array.from({ length: 24 }, () => ({ total: 0, count: 0 }));
          data.forEach(item => {
              const hour = item.date.getHours();
              hourlyData[hour].total += item.consumed;
              hourlyData[hour].count += 1;
          });
          return hourlyData.map((hourData, hour) => ({ name: `${hour}:00`, 'Avg Consumption': hourData.count > 0 ? (hourData.total / hourData.count).toFixed(0) : 0 }));
      };
      return (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
            <SummaryCard title="Today's Total" value={`${todayConsumption.toFixed(0)} ml`} icon={<BottleIcon className="w-8 h-8 text-orange-500"/>} borderColor="border-orange-200" />
            <SummaryCard title="Yesterday's Total" value={`${yesterdayConsumption.toFixed(0)} ml`} icon={<CalendarIcon className="w-8 h-8 text-blue-500" />} borderColor="border-blue-200" />
            <SummaryCard title="Daily Average" value={`${averageConsumption} ml`} icon={<ChartIcon className="w-8 h-8 text-green-500" />} borderColor="border-green-200" />
          </div>
          <div className="bg-white rounded-2xl shadow-lg p-6 mb-8">
              <h2 className="text-2xl font-bold text-gray-700 mb-4">Deeper Insights</h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                  <InsightCard title="Last 24h Total" value={`${last24hConsumption.toFixed(0)} ml`} icon={<ClockIcon className="w-7 h-7 text-purple-500"/>} />
                  <InsightCard title="Avg. Feeds per Day" value={avgFeedsPerDay} icon={<SunIcon className="w-7 h-7 text-yellow-500"/>} />
                  <InsightCard title="Wasted (Last 7 Days)" value={`${totalWastedLast7Days.toFixed(0)} ml`} icon={<TrashIcon className="w-7 h-7 text-red-500"/>} />
                  <InsightCard title="Most Recent Feed" value={data.length > 0 ? data[0].date.toLocaleTimeString('en-US', {hour: '2-digit', minute:'2-digit'}) : 'N_A'} icon={<ClockIcon className="w-7 h-7 text-indigo-500"/>} />
              </div>
          </div>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
              <div className="bg-white rounded-2xl shadow-lg p-4 sm:p-6">
                  <h2 className="text-2xl font-bold text-gray-700 mb-4">Daily Totals (Last 7 Days)</h2>
                  <div style={{ width: '100%', height: 300 }}><ResponsiveContainer><BarChart data={dailyBreakdownChartData} margin={{ top: 5, right: 20, left: -10, bottom: 5 }}><CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0"/><XAxis dataKey="name" stroke="#666" /><YAxis stroke="#666" /><Tooltip wrapperClassName="rounded-md shadow-md" /><Legend /><Bar dataKey="Consumed" stackId="a" fill="#3b82f6" /><Bar dataKey="Wasted" stackId="a" fill="#f59e0b" /></BarChart></ResponsiveContainer></div>
              </div>
              <div className="bg-white rounded-2xl shadow-lg p-4 sm:p-6">
                  <h2 className="text-2xl font-bold text-gray-700 mb-4">Last 7 Days Trend (ml)</h2>
                  <div style={{ width: '100%', height: 300 }}><ResponsiveContainer><LineChart data={lineChartData} margin={{ top: 5, right: 20, left: -10, bottom: 5 }}><CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" /><XAxis dataKey="name" stroke="#666" /><YAxis stroke="#666" /><Tooltip wrapperClassName="rounded-md shadow-md" /><Legend /><Line type="monotone" dataKey="Consumption" stroke="#16a34a" strokeWidth={3} dot={{ r: 5 }} activeDot={{ r: 8 }} /></LineChart></ResponsiveContainer></div>
              </div>
          </div>
          <div className="bg-white rounded-2xl shadow-lg p-4 sm:p-6 mb-8">
              <h2 className="text-2xl font-bold text-gray-700 mb-4">Average Consumption by Hour</h2>
              <div style={{ width: '100%', height: 300 }}><ResponsiveContainer><BarChart data={hourlyAvgChartData()} margin={{ top: 5, right: 20, left: -10, bottom: 5 }}><CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0"/><XAxis dataKey="name" stroke="#666" /><YAxis stroke="#666" /><Tooltip wrapperClassName="rounded-md shadow-md" /><Bar dataKey="Avg Consumption" fill="#8b5cf6" /></BarChart></ResponsiveContainer></div>
          </div>
          <div className="bg-white rounded-2xl shadow-lg">
              <h2 className="text-2xl font-bold text-gray-700 p-6 border-b border-gray-200">Recent Feeds</h2>
              <div className="divide-y divide-gray-200">{data.slice(0, 15).map((item, index) => (<LogItem key={index} item={item} />))}</div>
          </div>
        </>
      );
  }
  
  function AddEntryComponent({ isSignedIn, handleSignIn, handleSignOut, onDataAdded, apiReady }) {
      const [dateTime, setDateTime] = useState(new Date());
      const [offered, setOffered] = useState('');
      const [consumed, setConsumed] = useState('');
      const [notes, setNotes] = useState('');
      const [isSubmitting, setIsSubmitting] = useState(false);
      const [submitStatus, setSubmitStatus] = useState({ message: '', type: '' });
      
      useEffect(() => {
          const timer = setInterval(() => setDateTime(new Date()), 60000);
          return () => clearInterval(timer);
      }, []);
  
      const handleSubmit = async (e) => {
          e.preventDefault();
          const offeredNum = parseFloat(offered);
          const consumedNum = parseFloat(consumed);
  
          if (isNaN(offeredNum) || isNaN(consumedNum) || offeredNum < 0 || consumedNum < 0 || consumedNum > offeredNum) {
              setSubmitStatus({ message: 'Please enter valid numbers. Consumed cannot be greater than offered.', type: 'error' });
              return;
          }
          setIsSubmitting(true);
          setSubmitStatus({ message: '', type: '' });
          const notConsumed = offeredNum - consumedNum;
          const finished = consumedNum === offeredNum;
          const dateStr = `${String(dateTime.getDate()).padStart(2, '0')}/${String(dateTime.getMonth() + 1).padStart(2, '0')}/${dateTime.getFullYear()}`;
          const timeStr = `${String(dateTime.getHours()).padStart(2, '0')}:${String(dateTime.getMinutes()).padStart(2, '0')}:${String(dateTime.getSeconds()).padStart(2, '0')}`;
          const values = [[dateStr, timeStr, offeredNum, finished, notes, consumedNum, notConsumed]];
  
          try {
              await window.gapi.client.sheets.spreadsheets.values.append({
                  spreadsheetId: '1tw86AUy7oP4KMWTi-phkF1Z6Dw3qkoioVdMedqmP2no',
                  range: 'Milk Tracking!A:G',
                  valueInputOption: 'USER_ENTERED',
                  resource: { values },
              });
              setSubmitStatus({ message: 'Entry added successfully!', type: 'success' });
              setOffered(''); setConsumed(''); setNotes('');
              onDataAdded();
          } catch (err) {
              console.error('Error adding data:', err);
              setSubmitStatus({ message: `Error: ${err.result?.error?.message || 'Could not add entry.'}`, type: 'error' });
          } finally {
              setIsSubmitting(false);
          }
      };
  
      if (!apiReady) return <div className="text-center p-10">Initializing Google API...</div>;
  
      if (!isSignedIn) {
          return (
              <div className="bg-white rounded-2xl shadow-lg p-8 text-center">
                  <h2 className="text-2xl font-bold text-gray-700 mb-2">Sign In Required</h2>
                  <p className="text-gray-500 mb-6">You need to sign in with your Google Account to add entries to the sheet.</p>
                  <button
                      onClick={handleSignIn}
                      className="inline-flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 transition-colors"
                  >
                      <GoogleIcon />
                      <span className="ml-3">Sign in with Google</span>
                  </button>
              </div>
          );
      }
      
      return (
          <div className="bg-white rounded-2xl shadow-lg p-6 sm:p-8">
              <div className="flex justify-between items-center mb-6">
                  <h2 className="text-2xl font-bold text-gray-700">Add a New Feed</h2>
                  <button onClick={handleSignOut} className="text-sm text-blue-500 hover:underline">Sign Out</button>
              </div>
              <form onSubmit={handleSubmit} className="space-y-6">
                  <div>
                      <label className="block text-sm font-medium text-gray-700">Date & Time</label>
                      <p className="text-lg p-3 bg-gray-100 rounded-md mt-1">{dateTime.toLocaleString()}</p>
                      <p className="text-xs text-gray-500 mt-1">Time updates automatically. The current time will be used upon submission.</p>
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                      <div>
                          <label htmlFor="offered" className="block text-sm font-medium text-gray-700">Milk Offered (ml)</label>
                          <input type="number" id="offered" value={offered} onChange={e => setOffered(e.target.value)} required className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"/>
                      </div>
                      <div>
                          <label htmlFor="consumed" className="block text-sm font-medium text-gray-700">Milk Consumed (ml)</label>
                          <input type="number" id="consumed" value={consumed} onChange={e => setConsumed(e.target.value)} required className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"/>
                      </div>
                  </div>
                   <div>
                      <label htmlFor="notes" className="block text-sm font-medium text-gray-700">Notes (Optional)</label>
                      <textarea id="notes" value={notes} onChange={e => setNotes(e.target.value)} rows="3" className="mt-1 block w-full px-3 py-2 bg-white border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"></textarea>
                  </div>
                  <div>
                      <button type="submit" disabled={isSubmitting} className="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-lg font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:bg-gray-400">
                          {isSubmitting ? 'Adding...' : 'Add Entry'}
                      </button>
                  </div>
                  {submitStatus.message && (
                      <div className={`p-4 rounded-md text-center ${submitStatus.type === 'success' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                          {submitStatus.message}
                      </div>
                  )}
              </form>
          </div>
      );
  }
  
  const SummaryCard = ({ title, value, icon, borderColor }) => (
      <div className={`bg-white p-6 rounded-2xl shadow-md flex items-center space-x-4 border-l-4 ${borderColor}`}>
          <div className="bg-gray-100 p-3 rounded-full">{icon}</div>
          <div><p className="text-base text-gray-500 font-medium">{title}</p><p className="text-2xl font-bold text-gray-800">{value}</p></div>
      </div>
  );
  const InsightCard = ({ title, value, icon }) => (
      <div className="bg-gray-50 p-4 rounded-xl flex items-center space-x-3">
          <div className="bg-white p-2 rounded-full">{icon}</div>
          <div><p className="text-sm text-gray-500 font-medium">{title}</p><p className="text-lg font-bold text-gray-800">{value}</p></div>
      </div>
  );
  const LogItem = ({ item }) => (
    <div className="flex items-center p-4 hover:bg-gray-50 transition-colors">
      <div className="text-center mr-4 w-16">
        <p className="font-bold text-gray-700">{item.date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short' })}</p>
        <p className="text-sm text-gray-500">{item.date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true })}</p>
      </div>
      <div className="flex-grow">
        <p className="text-lg text-gray-800"><span className="font-bold">{item.consumed}ml</span> consumed</p>
        <p className="text-sm text-gray-400">(Offered {item.milkVolume}ml, {item.notConsumed}ml left)</p>
        {item.notes ? <p className="text-sm text-gray-600 italic mt-1">Note: {item.notes}</p> : null}
      </div>
      <div title={item.finished ? 'Finished the bottle' : 'Left some milk'} className={`w-3 h-3 rounded-full ml-4 ${item.finished ? 'bg-green-500' : 'bg-yellow-500'}`} />
    </div>
  );
  
  